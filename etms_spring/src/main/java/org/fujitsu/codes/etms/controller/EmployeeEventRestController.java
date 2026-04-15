package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.fujitsu.codes.etms.model.dto.EmployeeEventRequest;
import org.fujitsu.codes.etms.model.dto.EmployeeEventResponse;
import org.fujitsu.codes.etms.security.BasicAuthInterceptor;
import org.fujitsu.codes.etms.service.AuditTrailService;
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
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/employee-events")
public class EmployeeEventRestController {

    private final EmployeeEventDao employeeEventDao;
    private final EmployeesDao employeesDao;
    private final DepartmentDao departmentDao;
    private final AuditTrailService auditTrailService;

    public EmployeeEventRestController(
            EmployeeEventDao employeeEventDao,
            EmployeesDao employeesDao,
            DepartmentDao departmentDao,
            AuditTrailService auditTrailService) {
        this.employeeEventDao = employeeEventDao;
        this.employeesDao = employeesDao;
        this.departmentDao = departmentDao;
        this.auditTrailService = auditTrailService;
    }

    @GetMapping("/{employeeEventId}")
    public ResponseEntity<?> getById(@PathVariable Long employeeEventId) {
        return employeeEventDao.findById(employeeEventId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(
                        item,
                        buildEmployeesByCode(),
                        buildDepartmentsByCode())))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Employee event not found")));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeEventResponse>> getAll() {
        Map<String, Employees> employeesByCode = buildEmployeesByCode();
        Map<String, Department> departmentsByCode = buildDepartmentsByCode();
        List<EmployeeEventResponse> data = employeeEventDao.findAll().stream()
                .map(item -> toResponse(item, employeesByCode, departmentsByCode))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeEventResponse>> search(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Map<String, Employees> employeesByCode = buildEmployeesByCode();
        Map<String, Department> departmentsByCode = buildDepartmentsByCode();
        List<EmployeeEventResponse> data = employeeEventDao.search(
                        employeeNumber,
                        eventType,
                        status,
                        keyword,
                        startDate,
                        endDate)
                .stream()
                .map(item -> toResponse(item, employeesByCode, departmentsByCode))
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EmployeeEventRequest request, HttpServletRequest httpRequest) {
        normalize(request);
        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
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

    @PutMapping("/{employeeEventId}")
    public ResponseEntity<?> update(
            @PathVariable Long employeeEventId,
            @Valid @RequestBody EmployeeEventRequest request,
            HttpServletRequest httpRequest) {
        normalize(request);

        if (employeeEventDao.findById(employeeEventId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employee event not found"));
        }

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
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

    @DeleteMapping("/{employeeEventId}")
    public ResponseEntity<?> delete(@PathVariable Long employeeEventId, HttpServletRequest httpRequest) {
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
        java.util.ArrayList<String> errors = new java.util.ArrayList<>();

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
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
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
}
