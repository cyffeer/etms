package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.Employees;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.EmployeeRequest;
import org.fujitsu.codes.etms.model.dto.EmployeeResponse;
import org.fujitsu.codes.etms.validator.EmployeeValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    private static final String EMPLOYEE_NOT_FOUND = "Employee not found";

    private final EmployeesDao employeesDao;
    private final EmployeeValidator employeeValidator;

    public EmployeeRestController(EmployeesDao employeesDao, EmployeeValidator employeeValidator) {
        this.employeesDao = employeesDao;
        this.employeeValidator = employeeValidator;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Validated @RequestBody EmployeeRequest request) {
        List<String> errors = employeeValidator.validateCreate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        Employees employee = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        Employees saved = employeesDao.save(employee);
        return ResponseEntity.created(URI.create("/api/employees/" + saved.getEmployeeId()))
                .body(ApiResponse.success("Employee created successfully", toResponse(saved)));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable("employeeId") Long employeeId) {
        return employeesDao.findById(employeeId)
                .<ResponseEntity<ApiResponse<EmployeeResponse>>>map(employee ->
                        ResponseEntity.ok(ApiResponse.success("Employee fetched successfully", toResponse(employee))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllEmployees(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        if (page == null && size == null) {
            var data = employeesDao.findAll().stream()
                    .map(this::toResponse)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully", data));
        }

        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        var data = employeesDao.findAll(page, size).stream()
                .map(this::toResponse)
                .toList();
        long totalElements = employeesDao.countAll();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully",
                java.util.Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchEmployees(
            @RequestParam(name = "employeeNumber", required = false) String employeeNumber,
            @RequestParam(name = "nameKeyword", required = false) String nameKeyword,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {

        var data = employeesDao.search(employeeNumber, nameKeyword, startDate, endDate).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully", data));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable("employeeId") Long employeeId,
            @Validated @RequestBody EmployeeRequest request) {

        if (employeesDao.findById(employeeId).isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND)));
        }

        List<String> errors = employeeValidator.validateUpdate(employeeId, request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        Employees source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return employeesDao.update(employeeId, source)
                .<ResponseEntity<ApiResponse<EmployeeResponse>>>map(employee ->
                        ResponseEntity.ok(ApiResponse.success("Employee updated successfully", toResponse(employee))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND))));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable("employeeId") Long employeeId) {
        boolean deleted = employeesDao.deleteById(employeeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND)));
        }
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }

    private Employees toEntity(EmployeeRequest request) {
        Employees employee = new Employees();
        employee.setEmployeeCode(request.getEmployeeCode() == null ? null : request.getEmployeeCode().trim());
        employee.setFirstName(request.getFirstName() == null ? null : request.getFirstName().trim());
        employee.setLastName(request.getLastName() == null ? null : request.getLastName().trim());
        employee.setEmail(request.getEmail() == null ? null : request.getEmail().trim());
        employee.setHireDate(request.getHireDate());
        employee.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return employee;
    }

    private EmployeeResponse toResponse(Employees employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setHireDate(employee.getHireDate());
        response.setActive(employee.getActive());
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());
        return response;
    }
}
