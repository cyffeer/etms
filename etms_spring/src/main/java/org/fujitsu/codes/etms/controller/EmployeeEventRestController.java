package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fujitsu.codes.etms.model.dao.DepartmentDao;
import org.fujitsu.codes.etms.model.dao.EmployeeEventDao;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.Department;
import org.fujitsu.codes.etms.model.data.EmployeeEvent;
import org.fujitsu.codes.etms.model.data.Employees;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.EmployeeEventRequest;
import org.fujitsu.codes.etms.model.dto.EmployeeEventResponse;
import org.fujitsu.codes.etms.model.dto.EmployeeEventSummaryItem;
import org.fujitsu.codes.etms.model.dto.EmployeeEventSummaryResponse;
import org.fujitsu.codes.etms.security.BasicAuthInterceptor;
import org.fujitsu.codes.etms.service.AuditTrailService;
import org.fujitsu.codes.etms.service.EtmsAuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/employee-events")
public class EmployeeEventRestController {

    private static final Map<String, String> EVENT_LABELS = Map.of(
            "PROMOTION", "Promotions",
            "VIOLATION", "Violations",
            "CITATION", "Citations",
            "PROJECT_ASSIGNMENT", "Project Assignments",
            "RESIGNATION", "Resignations",
            "SUSPENSION", "Suspensions",
            "TERMINATION", "Terminations");

    private final EmployeeEventDao employeeEventDao;
    private final EmployeesDao employeesDao;
    private final DepartmentDao departmentDao;
    private final AuditTrailService auditTrailService;
    private final EtmsAuthorizationService etmsAuthorizationService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmployeeEventRestController.class);

    public EmployeeEventRestController(
            EmployeeEventDao employeeEventDao,
            EmployeesDao employeesDao,
            DepartmentDao departmentDao,
            AuditTrailService auditTrailService,
            EtmsAuthorizationService etmsAuthorizationService) {
        this.employeeEventDao = employeeEventDao;
        this.employeesDao = employeesDao;
        this.departmentDao = departmentDao;
        this.auditTrailService = auditTrailService;
        this.etmsAuthorizationService = etmsAuthorizationService;
    }

    @GetMapping("/{employeeEventId:\\d+}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getById(Authentication authentication, @PathVariable Long employeeEventId) {
        log.info("Employee event detail requested for id={}", employeeEventId);
        EmployeeEvent event = employeeEventDao.findById(employeeEventId).orElse(null);
        if (event == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Employee event not found"));
        }
        if (!etmsAuthorizationService.canViewEmployeeEvent(authentication, event.getEventType(), event.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }
        return ResponseEntity.ok(toResponse(
                event,
                buildEmployeesByCode(),
                buildDepartmentsByCode()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getAll(
            Authentication authentication,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        log.info("Employee event list requested page={} size={}", page, size);
        Map<String, Employees> employeesByCode = buildEmployeesByCode();
        Map<String, Department> departmentsByCode = buildDepartmentsByCode();
        List<EmployeeEventResponse> allItems = employeeEventDao.findAll().stream()
                .filter(item -> etmsAuthorizationService.canViewEmployeeEvent(authentication, item.getEventType(), item.getEmployeeNumber()))
                .map(item -> toResponse(item, employeesByCode, departmentsByCode))
                .toList();

        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Employee events fetched successfully", allItems));
        }

        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, allItems.size());
        int toIndex = Math.min(fromIndex + size, allItems.size());
        List<EmployeeEventResponse> data = allItems.subList(fromIndex, toIndex);
        long totalElements = allItems.size();
        long totalPages = size <= 0 ? 0L : (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Employee events fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EmployeeEventSummaryResponse>> summary(Authentication authentication) {
        log.info("Employee event summary requested");
        List<EmployeeEvent> all = employeeEventDao.findAll().stream()
                .filter(item -> etmsAuthorizationService.canViewEmployeeEvent(authentication, item.getEventType(), item.getEmployeeNumber()))
                .toList();
        EmployeeEventSummaryResponse response = new EmployeeEventSummaryResponse();
        response.setTotalCount(all.size());
        response.setActiveCount(all.stream().filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus())).count());
        response.setPendingCount(all.stream().filter(item -> "PENDING".equalsIgnoreCase(item.getStatus())).count());
        response.setClosedCount(all.stream().filter(item -> "CLOSED".equalsIgnoreCase(item.getStatus())).count());

        Map<String, Long> counts = all.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getEventType() == null ? "" : item.getEventType().toUpperCase(),
                        LinkedHashMap::new,
                        Collectors.counting()));

        List<EmployeeEventSummaryItem> categories = new ArrayList<>();
        EVENT_LABELS.forEach((key, label) -> {
            EmployeeEventSummaryItem item = new EmployeeEventSummaryItem();
            item.setEventType(key);
            item.setLabel(label);
            item.setCount(counts.getOrDefault(key, 0L));
            categories.add(item);
        });
        response.setCategories(categories);

        return ResponseEntity.ok(ApiResponse.success("Employee event summary fetched successfully", response));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> search(
            Authentication authentication,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        if (hasRole(authentication, "EMPLOYEE")) {
            String currentEmployeeNumber = resolveEmployeeNumberForCurrentUser(authentication);
            if (currentEmployeeNumber != null) {
                employeeNumber = currentEmployeeNumber;
            }
        } else {
            employeeNumber = normalizeEmployeeInput(employeeNumber);
        }
        if (employeeNumber != null && !employeeNumber.isBlank()
                && !etmsAuthorizationService.canAccessEmployeeInput(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }
        Map<String, Employees> employeesByCode = buildEmployeesByCode();
        Map<String, Department> departmentsByCode = buildDepartmentsByCode();
        log.info("Employee event search requested eventType={} employeeNumber={}", eventType, employeeNumber);
        List<EmployeeEventResponse> allItems = employeeEventDao.search(
                        employeeNumber,
                        eventType,
                        status,
                        keyword,
                        startDate,
                        endDate)
                .stream()
                .map(item -> toResponse(item, employeesByCode, departmentsByCode))
                .filter(item -> etmsAuthorizationService.canViewEmployeeEvent(authentication, item.getEventType(), item.getEmployeeNumber()))
                .toList();

        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Employee events fetched successfully", allItems));
        }

        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, allItems.size());
        int toIndex = Math.min(fromIndex + size, allItems.size());
        List<EmployeeEventResponse> data = allItems.subList(fromIndex, toIndex);
        long totalElements = allItems.size();
        long totalPages = size <= 0 ? 0L : (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Employee events fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    @GetMapping("/category/{eventType}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getByCategory(
            Authentication authentication,
            @PathVariable String eventType,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        String normalizedType = normalizeEventType(eventType);
        if (normalizedType == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Unknown event type", List.of("eventType is not supported")));
        }

        Map<String, Employees> employeesByCode = buildEmployeesByCode();
        Map<String, Department> departmentsByCode = buildDepartmentsByCode();
        List<EmployeeEventResponse> allItems = employeeEventDao.search(
                        null,
                        normalizedType,
                        null,
                        null,
                        null,
                        null)
                .stream()
                .map(item -> toResponse(item, employeesByCode, departmentsByCode))
                .filter(item -> etmsAuthorizationService.canViewEmployeeEvent(authentication, item.getEventType(), item.getEmployeeNumber()))
                .toList();

        return paginate(allItems, page, size);
    }

    @GetMapping("/category/{eventType}/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> searchByCategory(
            Authentication authentication,
            @PathVariable String eventType,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        String normalizedType = normalizeEventType(eventType);
        if (normalizedType == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Unknown event type", List.of("eventType is not supported")));
        }
        if (hasRole(authentication, "EMPLOYEE")) {
            String currentEmployeeNumber = resolveEmployeeNumberForCurrentUser(authentication);
            if (currentEmployeeNumber != null) {
                employeeNumber = currentEmployeeNumber;
            }
        } else {
            employeeNumber = normalizeEmployeeInput(employeeNumber);
        }
        if (employeeNumber != null && !employeeNumber.isBlank()
                && !etmsAuthorizationService.canAccessEmployeeInput(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }

        Map<String, Employees> employeesByCode = buildEmployeesByCode();
        Map<String, Department> departmentsByCode = buildDepartmentsByCode();
        log.info("Employee event category search requested category={} employeeNumber={}", normalizedType, employeeNumber);
        List<EmployeeEventResponse> allItems = employeeEventDao.search(
                        employeeNumber,
                        normalizedType,
                        status,
                        keyword,
                        startDate,
                        endDate)
                .stream()
                .map(item -> toResponse(item, employeesByCode, departmentsByCode))
                .filter(item -> etmsAuthorizationService.canViewEmployeeEvent(authentication, item.getEventType(), item.getEmployeeNumber()))
                .toList();

        return paginate(allItems, page, size);
    }

    @PostMapping
    @PreAuthorize("@etmsAuthorizationService.canMaintainEmployeeEvent(authentication, #request.eventType, #request.employeeNumber)")
    public ResponseEntity<?> create(Authentication authentication, @Valid @RequestBody EmployeeEventRequest request, HttpServletRequest httpRequest) {
        normalize(request);
        if (hasRole(authentication, "EMPLOYEE")) {
            String currentEmployeeNumber = resolveEmployeeNumberForCurrentUser(authentication);
            request.setEmployeeNumber(currentEmployeeNumber == null ? authentication.getName() : currentEmployeeNumber);
            request.setEventType("RESIGNATION");
            request.setStatus("PENDING");
        }
        log.info("Employee event create requested type={} employee={}", request.getEventType(), request.getEmployeeNumber());
        if (!etmsAuthorizationService.canMaintainEmployeeEvent(authentication, request.getEventType(), request.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }
        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        EmployeeEvent entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        EmployeeEvent saved = employeeEventDao.save(entity);
        auditTrailService.log(
                getAuthenticatedUser(httpRequest),
                "CREATE",
                "EMPLOYEE_EVENT",
                String.valueOf(saved.getEmployeeEventId()),
                "Created " + saved.getEventType() + " event for employee " + saved.getEmployeeNumber(),
                httpRequest);
        return ResponseEntity.created(URI.create("/api/employee-events/" + saved.getEmployeeEventId()))
                .body(toResponse(saved, buildEmployeesByCode(), buildDepartmentsByCode()));
    }

    @PutMapping("/{employeeEventId:\\d+}")
    @PreAuthorize("@etmsAuthorizationService.canMaintainEmployeeEvent(authentication, #request.eventType, #request.employeeNumber)")
    public ResponseEntity<?> update(
            Authentication authentication,
            @PathVariable Long employeeEventId,
            @Valid @RequestBody EmployeeEventRequest request,
            HttpServletRequest httpRequest) {
        log.info("Employee event update requested id={} type={}", employeeEventId, request.getEventType());
        EmployeeEvent existing = employeeEventDao.findById(employeeEventId).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Employee event not found"));
        }
        if (!etmsAuthorizationService.canMaintainEmployeeEvent(authentication, existing.getEventType(), existing.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }
        if (hasRole(authentication, "EMPLOYEE")) {
            if (!"PENDING".equalsIgnoreCase(existing.getStatus())) {
                return ResponseEntity.status(403).body(Map.of("message", "Only pending resignation requests can be updated"));
            }
            request.setEmployeeNumber(existing.getEmployeeNumber());
            request.setEventType(existing.getEventType());
            request.setStatus("PENDING");
        }
        normalize(request);

        if (!etmsAuthorizationService.canMaintainEmployeeEvent(authentication, request.getEventType(), request.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        EmployeeEvent source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return employeeEventDao.update(employeeEventId, source)
                .<ResponseEntity<?>>map(item -> {
                    auditTrailService.log(
                            getAuthenticatedUser(httpRequest),
                            "UPDATE",
                            "EMPLOYEE_EVENT",
                            String.valueOf(item.getEmployeeEventId()),
                            "Updated " + item.getEventType() + " event for employee " + item.getEmployeeNumber(),
                            httpRequest);
                    return ResponseEntity.ok(toResponse(
                            item,
                            buildEmployeesByCode(),
                            buildDepartmentsByCode()));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Employee event not found")));
    }

    @DeleteMapping("/{employeeEventId:\\d+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long employeeEventId, HttpServletRequest httpRequest) {
        log.info("Employee event delete requested id={}", employeeEventId);
        EmployeeEvent existing = employeeEventDao.findById(employeeEventId).orElse(null);
        boolean deleted = employeeEventDao.deleteById(employeeEventId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Employee event not found"));
        }
        if (existing != null) {
            auditTrailService.log(
                    getAuthenticatedUser(httpRequest),
                    "DELETE",
                    "EMPLOYEE_EVENT",
                    String.valueOf(existing.getEmployeeEventId()),
                    "Deleted " + existing.getEventType() + " event for employee " + existing.getEmployeeNumber(),
                    httpRequest);
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(EmployeeEventRequest request) {
        ArrayList<String> errors = new ArrayList<>();

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }
        if (request.getDepartmentCode() != null
                && !request.getDepartmentCode().isBlank()
                && !departmentDao.existsByDepartmentCode(request.getDepartmentCode())) {
            errors.add("Department code does not exist");
        }
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getEffectiveDate())) {
            errors.add("End date cannot be before effective date");
        }
        return errors;
    }

    private void normalize(EmployeeEventRequest request) {
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(normalizeEmployeeInput(request.getEmployeeNumber()));
        }
        if (request.getEventType() != null) {
            request.setEventType(request.getEventType().trim().toUpperCase());
        }
        if (request.getTitle() != null) {
            request.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            request.setDescription(request.getDescription().trim());
        }
        if (request.getDepartmentCode() != null) {
            request.setDepartmentCode(request.getDepartmentCode().trim());
        }
        if (request.getReferenceCode() != null) {
            request.setReferenceCode(request.getReferenceCode().trim());
        }
        if (request.getStatus() != null) {
            request.setStatus(request.getStatus().trim().toUpperCase());
        }
    }

    private EmployeeEvent toEntity(EmployeeEventRequest request) {
        EmployeeEvent entity = new EmployeeEvent();
        entity.setEmployeeNumber(request.getEmployeeNumber());
        entity.setEventType(request.getEventType());
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setDepartmentCode(request.getDepartmentCode());
        entity.setReferenceCode(request.getReferenceCode());
        entity.setEffectiveDate(request.getEffectiveDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(request.getStatus());
        return entity;
    }

    private EmployeeEventResponse toResponse(
            EmployeeEvent entity,
            Map<String, Employees> employeesByCode,
            Map<String, Department> departmentsByCode) {
        EmployeeEventResponse response = new EmployeeEventResponse();
        response.setEmployeeEventId(entity.getEmployeeEventId());
        response.setEmployeeNumber(entity.getEmployeeNumber());
        response.setEventType(entity.getEventType());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setDepartmentCode(entity.getDepartmentCode());
        response.setReferenceCode(entity.getReferenceCode());
        response.setEffectiveDate(entity.getEffectiveDate());
        response.setEndDate(entity.getEndDate());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        Employees employee = employeesByCode.get(entity.getEmployeeNumber());
        if (employee != null) {
            response.setEmployeeName((employee.getFirstName() + " " + employee.getLastName()).trim());
        }

        Department department = departmentsByCode.get(entity.getDepartmentCode());
        if (department != null) {
            response.setDepartmentName(department.getDepartmentName());
        }
        return response;
    }

    private ResponseEntity<ApiResponse<?>> paginate(List<EmployeeEventResponse> allItems, Integer page, Integer size) {
        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Employee events fetched successfully", allItems));
        }

        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, allItems.size());
        int toIndex = Math.min(fromIndex + size, allItems.size());
        List<EmployeeEventResponse> data = allItems.subList(fromIndex, toIndex);
        long totalElements = allItems.size();
        long totalPages = size <= 0 ? 0L : (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Employee events fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    private String normalizeEventType(String eventType) {
        if (eventType == null || eventType.isBlank()) {
            return null;
        }
        String normalized = eventType.trim().toUpperCase();
        return EVENT_LABELS.containsKey(normalized) ? normalized : null;
    }

    private Map<String, Employees> buildEmployeesByCode() {
        return employeesDao.findAll().stream()
                .collect(Collectors.toMap(Employees::getEmployeeCode, Function.identity(), (left, right) -> left));
    }

    private Map<String, Department> buildDepartmentsByCode() {
        return departmentDao.findAll().stream()
                .collect(Collectors.toMap(Department::getDepartmentCode, Function.identity(), (left, right) -> left));
    }

    private Login getAuthenticatedUser(HttpServletRequest request) {
        Object value = request == null ? null : request.getAttribute(BasicAuthInterceptor.AUTHENTICATED_USER_ATTR);
        return value instanceof Login login ? login : null;
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || role == null || role.isBlank()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.toUpperCase()));
    }

    private String resolveEmployeeNumberForCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }

        String principal = authentication.getName().trim();
        if (employeesDao.existsByEmployeeCode(principal)) {
            return principal;
        }

        String digits = principal.replaceAll("\\D+", "");
        if (digits.isBlank()) {
            return null;
        }

        try {
            Long employeeId = Long.valueOf(digits);
            return employeesDao.findById(employeeId)
                    .map(Employees::getEmployeeCode)
                    .orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeEmployeeInput(String employeeInput) {
        if (employeeInput == null || employeeInput.isBlank()) {
            return employeeInput;
        }

        String normalized = employeeInput.trim();
        return employeesDao.findByEmployeeCode(normalized)
                .map(Employees::getEmployeeCode)
                .or(() -> {
                    Integer employeeId = employeesDao.resolveEmployeeIdentifier(normalized);
                    if (employeeId == null) {
                        return java.util.Optional.empty();
                    }
                    return employeesDao.findById(employeeId.longValue()).map(Employees::getEmployeeCode);
                })
                .orElse(normalized);
    }
}
