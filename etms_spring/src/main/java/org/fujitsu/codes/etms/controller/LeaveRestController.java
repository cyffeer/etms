package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.dao.LeaveTypeDao;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.fujitsu.codes.etms.model.data.LeaveType;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.LeaveBalanceResponse;
import org.fujitsu.codes.etms.model.dto.LeaveRequest;
import org.fujitsu.codes.etms.model.dto.LeaveResponse;
import org.fujitsu.codes.etms.service.LeaveBalanceService;
import org.fujitsu.codes.etms.validator.LeaveValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRestController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LeaveRestController.class);

    private final LeaveDao leaveDao;
    private final EmployeesDao employeesDao;
    private final LeaveTypeDao leaveTypeDao;
    private final LeaveBalanceService leaveBalanceService;

    @Autowired
    public LeaveRestController(LeaveDao leaveDao, EmployeesDao employeesDao, LeaveTypeDao leaveTypeDao,
            LeaveBalanceService leaveBalanceService) {
        this.leaveDao = leaveDao;
        this.employeesDao = employeesDao;
        this.leaveTypeDao = leaveTypeDao;
        this.leaveBalanceService = leaveBalanceService;
    }

    public LeaveRestController(LeaveDao leaveDao, EmployeesDao employeesDao, LeaveTypeDao leaveTypeDao) {
        this(leaveDao, employeesDao, leaveTypeDao, null);
    }

    @GetMapping("/{leaveRecordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getById(Authentication authentication, @PathVariable("leaveRecordId") Long leaveRecordId) {
        log.info("Leave detail requested for id={}", leaveRecordId);
        LeaveRecord leave = leaveDao.findById(leaveRecordId).orElse(null);
        if (leave == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Leave not found", List.of("Leave not found")));
        }
        if (!canAccess(authentication, leave.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }
        return ResponseEntity.ok(ApiResponse.success("Leave fetched successfully", toResponse(leave)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getAll(
            Authentication authentication,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        log.info("Leave list requested");
        List<LeaveResponse> allItems = leaveDao.findAll().stream()
                .map(this::toResponse)
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();

        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", allItems));
        }
        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, allItems.size());
        int toIndex = Math.min(fromIndex + size, allItems.size());
        List<LeaveResponse> data = allItems.subList(fromIndex, toIndex);
        long totalElements = allItems.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    @GetMapping("/employee/{employeeNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getByEmployee(Authentication authentication, @PathVariable("employeeNumber") String employeeNumber) {
        log.info("Leave-by-employee requested for employeeNumber={}", employeeNumber);
        if (!canAccess(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }
        List<LeaveResponse> data = leaveDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", data));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getByStatus(Authentication authentication, @PathVariable("status") String status) {
        List<LeaveResponse> data = leaveDao.findByStatus(status).stream()
                .map(this::toResponse)
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", data));
    }

    @GetMapping("/balances")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getBalances(
            Authentication authentication,
            @RequestParam(name = "employeeNumber", required = false) String employeeNumber,
            @RequestParam(name = "year", required = false) Integer year) {
        if (isEmployee(authentication)) {
            String currentEmployeeNumber = resolveEmployeeNumberForCurrentUser(authentication);
            if (currentEmployeeNumber != null) {
                employeeNumber = currentEmployeeNumber;
            }
        }
        log.info("Leave balances requested for employeeNumber={}", employeeNumber);
        if (!canAccess(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }
        if (leaveBalanceService == null) {
            return ResponseEntity.ok(ApiResponse.success("Leave balances fetched successfully", List.of()));
        }
        List<LeaveBalanceResponse> data = leaveBalanceService.getBalances(employeeNumber, year);
        return ResponseEntity.ok(ApiResponse.success("Leave balances fetched successfully", data));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> search(
            Authentication authentication,
            @RequestParam(name = "employeeNumber", required = false) String employeeNumber,
            @RequestParam(name = "leaveType", required = false) String leaveType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size) {
        log.info("Leave search requested");
        if (isEmployee(authentication)) {
            String currentEmployeeNumber = resolveEmployeeNumberForCurrentUser(authentication);
            if (currentEmployeeNumber != null) {
                employeeNumber = currentEmployeeNumber;
            }
        }
        if (employeeNumber != null && !employeeNumber.isBlank() && !canAccess(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }

        List<LeaveResponse> allItems = (startDate == null && endDate == null
                ? leaveDao.search(employeeNumber, leaveType, status)
                : leaveDao.search(employeeNumber, leaveType, status, startDate, endDate)).stream()
                .map(this::toResponse)
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();

        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", allItems));
        }
        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, allItems.size());
        int toIndex = Math.min(fromIndex + size, allItems.size());
        List<LeaveResponse> data = allItems.subList(fromIndex, toIndex);
        long totalElements = allItems.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> create(Authentication authentication, @Valid @RequestBody LeaveRequest request) {
        normalize(request);
        log.info("Leave create requested for employeeNumber={}", request.getEmployeeNumber());
        if (isEmployee(authentication)) {
            String currentEmployeeNumber = resolveEmployeeNumberForCurrentUser(authentication);
            request.setEmployeeNumber(currentEmployeeNumber == null ? authentication.getName() : currentEmployeeNumber);
            request.setStatus("PENDING");
        }
        if (!canAccess(authentication, request.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }
        List<String> errors = LeaveValidator.validate(request);
        if (!errors.isEmpty()) {
            log.warn("Leave create validation failed for employeeNumber={}", request.getEmployeeNumber());
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed",
                    List.of("Employee number does not exist")));
        }

        LeaveType leaveType = resolveLeaveType(request.getLeaveType());
        if (leaveType == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed",
                    List.of("Leave type does not exist")));
        }

        LeaveRecord entity = toEntity(request, leaveType);
        LeaveRecord saved = leaveDao.save(entity);
        log.info("Leave create succeeded for employeeNumber={}", request.getEmployeeNumber());
        return ResponseEntity.created(URI.create("/api/leaves/" + saved.getLeaveRecordId()))
                .body(ApiResponse.success("Leave created successfully", toResponse(saved)));
    }

    @PutMapping("/{leaveRecordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> update(
            Authentication authentication,
            @PathVariable("leaveRecordId") Long leaveRecordId,
            @Valid @RequestBody LeaveRequest request) {
        normalize(request);
        log.info("Leave update requested for id={}", leaveRecordId);
        LeaveRecord existing = leaveDao.findById(leaveRecordId).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Leave not found", List.of("Leave not found")));
        }
        if (!canAccess(authentication, existing.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }

        if (isEmployee(authentication)) {
            if (!"PENDING".equalsIgnoreCase(existing.getStatus())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Only pending leave requests can be updated")));
            }
            request.setEmployeeNumber(existing.getEmployeeNumber());
            request.setStatus("PENDING");
        }

        if (!canAccess(authentication, request.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }

        List<String> errors = LeaveValidator.validate(request);
        if (!errors.isEmpty()) {
            log.warn("Leave update validation failed for id={}", leaveRecordId);
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        LeaveType leaveType = resolveLeaveType(request.getLeaveType());
        if (leaveType == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed",
                    List.of("Leave type does not exist")));
        }

        LeaveRecord entity = toEntity(request, leaveType);
        return leaveDao.update(leaveRecordId, entity)
                .<ResponseEntity<ApiResponse<?>>>map(l ->
                        ResponseEntity.ok(ApiResponse.success("Leave updated successfully", toResponse(l))))
                .orElseGet(() ->
                        ResponseEntity.status(404).body(ApiResponse.error("Leave not found", List.of("Leave not found"))));
    }

    @DeleteMapping("/{leaveRecordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("leaveRecordId") Long leaveRecordId) {
        log.info("Leave delete requested for id={}", leaveRecordId);
        boolean deleted = leaveDao.deleteById(leaveRecordId);
        if (!deleted) {
            return ResponseEntity.status(404).body(ApiResponse.error("Leave not found", List.of("Leave not found")));
        }
        return ResponseEntity.ok(ApiResponse.success("Leave deleted successfully", null));
    }

    private LeaveType resolveLeaveType(String leaveTypeValue) {
        if (leaveTypeValue == null || leaveTypeValue.isBlank()) {
            return null;
        }
        return leaveTypeDao.findByCodeOrName(leaveTypeValue.trim()).orElse(null);
    }

    private LeaveRecord toEntity(LeaveRequest request, LeaveType leaveType) {
        LeaveRecord leave = new LeaveRecord();
        leave.setEmployeeNumber(request.getEmployeeNumber());
        leave.setLeaveType(leaveType.getLeaveTypeCode());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setStatus(request.getStatus());
        leave.setRemarks(request.getRemarks());
        return leave;
    }

    private LeaveResponse toResponse(LeaveRecord leave) {
        LeaveResponse response = new LeaveResponse();
        response.setLeaveRecordId(leave.getLeaveRecordId());
        response.setEmployeeNumber(leave.getEmployeeNumber());
        response.setLeaveType(leave.getLeaveType());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setStatus(leave.getStatus());
        response.setRemarks(leave.getRemarks());
        return response;
    }

    private boolean canAccess(Authentication authentication, String employeeNumber) {
        if (authentication == null || employeeNumber == null) {
            return false;
        }
        if (hasAnyRole(authentication, "ADMIN", "HR", "MANAGER")) {
            return true;
        }
        if (authentication.getName() == null) {
            return false;
        }
        String current = authentication.getName().trim();
        String candidate = employeeNumber.trim();
        if (current.equalsIgnoreCase(candidate)) {
            return true;
        }
        String resolvedCurrentEmployeeNumber = resolveEmployeeNumberForCurrentUser(authentication);
        if (resolvedCurrentEmployeeNumber != null && resolvedCurrentEmployeeNumber.equalsIgnoreCase(candidate)) {
            return true;
        }
        return current.replaceAll("\\D+", "").equals(candidate.replaceAll("\\D+", ""));
    }

    private boolean hasAnyRole(Authentication authentication, String... roles) {
        if (authentication == null) {
            return false;
        }
        return java.util.Arrays.stream(roles)
                .anyMatch(role -> authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)));
    }

    private boolean isEmployee(Authentication authentication) {
        return hasAnyRole(authentication, "EMPLOYEE") && !hasAnyRole(authentication, "ADMIN", "HR", "MANAGER");
    }

    private void normalize(LeaveRequest request) {
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
        if (request.getLeaveType() != null) {
            request.setLeaveType(request.getLeaveType().trim());
        }
        if (request.getStatus() != null) {
            request.setStatus(request.getStatus().trim().toUpperCase());
        }
        if (request.getRemarks() != null) {
            request.setRemarks(request.getRemarks().trim());
        }
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
                    .map(org.fujitsu.codes.etms.model.data.Employees::getEmployeeCode)
                    .orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
