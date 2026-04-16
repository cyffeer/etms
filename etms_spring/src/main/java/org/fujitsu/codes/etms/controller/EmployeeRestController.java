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
import org.fujitsu.codes.etms.model.dto.EmployeeSelfUpdateRequest;
import org.fujitsu.codes.etms.service.EmployeePhotoService;
import org.fujitsu.codes.etms.validator.EmployeeValidator;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    private static final String EMPLOYEE_NOT_FOUND = "Employee not found";
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmployeeRestController.class);

    private final EmployeesDao employeesDao;
    private final EmployeeValidator employeeValidator;
    private final EmployeePhotoService employeePhotoService;

    @Autowired
    public EmployeeRestController(
            EmployeesDao employeesDao,
            EmployeeValidator employeeValidator,
            EmployeePhotoService employeePhotoService) {
        this.employeesDao = employeesDao;
        this.employeeValidator = employeeValidator;
        this.employeePhotoService = employeePhotoService;
    }

    public EmployeeRestController(EmployeesDao employeesDao, EmployeeValidator employeeValidator) {
        this(employeesDao, employeeValidator, null);
    }

    @PostMapping
    @PreAuthorize("@etmsAuthorizationService.canManageEmployeeRecords(authentication)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Validated @RequestBody EmployeeRequest request) {
        log.info("Employee create requested");
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

    @GetMapping("/{employeeId:\\d+}")
    @PreAuthorize("@etmsAuthorizationService.canViewEmployee(authentication, #employeeId)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable("employeeId") Long employeeId) {
        log.info("Employee fetch requested for id={}", employeeId);
        return employeesDao.findById(employeeId)
                .<ResponseEntity<ApiResponse<EmployeeResponse>>>map(employee ->
                        ResponseEntity.ok(ApiResponse.success("Employee fetched successfully", toResponse(employee))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND))));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getMyProfile(Authentication authentication) {
        Long employeeId = resolveAuthenticatedEmployeeId(authentication);
        if (employeeId == null) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }

        return employeesDao.findById(employeeId)
                .<ResponseEntity<ApiResponse<EmployeeResponse>>>map(employee ->
                        ResponseEntity.ok(ApiResponse.success("Employee fetched successfully", toResponse(employee))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND))));
    }

    @PostMapping(path = "/{employeeId:\\d+}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@etmsAuthorizationService.canViewEmployee(authentication, #employeeId)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> uploadEmployeePhoto(
            @PathVariable("employeeId") Long employeeId,
            @RequestParam("file") MultipartFile file) {
        log.info("Employee photo upload requested for id={}", employeeId);

        Employees employee = employeesDao.findById(employeeId).orElse(null);
        if (employee == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND)));
        }

        String storedPhoto = employeePhotoService.storePhoto(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                file,
                employee.getPhotoPath());

        Employees updated = employeesDao.updatePhoto(employeeId, storedPhoto, LocalDateTime.now())
                .orElse(employee);

        return ResponseEntity.ok(ApiResponse.success("Employee photo uploaded successfully", toResponse(updated)));
    }

    @GetMapping("/{employeeId:\\d+}/photo")
    @PreAuthorize("@etmsAuthorizationService.canViewEmployee(authentication, #employeeId)")
    public ResponseEntity<Resource> getEmployeePhoto(@PathVariable("employeeId") Long employeeId) {
        log.info("Employee photo fetch requested for id={}", employeeId);
        Employees employee = employeesDao.getByIdOrThrow(employeeId);
        Resource resource = employeePhotoService.loadPhoto(employee.getPhotoPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(employeePhotoService.detectContentType(resource)));
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(resource.getFilename() == null ? "employee-photo" : resource.getFilename())
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping
    @PreAuthorize("@etmsAuthorizationService.canManageEmployeeRecords(authentication)")
    public ResponseEntity<ApiResponse<?>> getAllEmployees(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        log.info("Employee list requested page={} size={}", page, size);

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
    @PreAuthorize("@etmsAuthorizationService.canManageEmployeeRecords(authentication)")
    public ResponseEntity<ApiResponse<?>> searchEmployees(
            @RequestParam(name = "employeeNumber", required = false) String employeeNumber,
            @RequestParam(name = "nameKeyword", required = false) String nameKeyword,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {
        log.info("Employee search requested");

        var data = employeesDao.search(employeeNumber, nameKeyword, startDate, endDate).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully", data));
    }

    @PutMapping("/{employeeId:\\d+}")
    @PreAuthorize("@etmsAuthorizationService.canManageEmployeeRecords(authentication)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable("employeeId") Long employeeId,
            @Validated @RequestBody EmployeeRequest request) {
        log.info("Employee update requested for id={}", employeeId);

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

    @PutMapping("/me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateMyProfile(
            Authentication authentication,
            @Validated @RequestBody EmployeeSelfUpdateRequest request) {
        Long employeeId = resolveAuthenticatedEmployeeId(authentication);
        if (employeeId == null) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", List.of("Forbidden")));
        }

        Employees existing = employeesDao.findById(employeeId).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND)));
        }

        EmployeeRequest updateRequest = new EmployeeRequest();
        updateRequest.setEmployeeCode(existing.getEmployeeCode());
        updateRequest.setFirstName(request.getFirstName() == null ? existing.getFirstName() : request.getFirstName().trim());
        updateRequest.setLastName(request.getLastName() == null ? existing.getLastName() : request.getLastName().trim());
        updateRequest.setEmail(request.getEmail() == null ? existing.getEmail() : request.getEmail().trim());
        updateRequest.setHireDate(existing.getHireDate());
        updateRequest.setActive(existing.getActive());

        List<String> errors = employeeValidator.validateUpdate(employeeId, updateRequest);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        Employees source = toEntity(updateRequest);
        source.setUpdatedAt(LocalDateTime.now());

        return employeesDao.update(employeeId, source)
                .<ResponseEntity<ApiResponse<EmployeeResponse>>>map(employee ->
                        ResponseEntity.ok(ApiResponse.success("Employee updated successfully", toResponse(employee))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error(EMPLOYEE_NOT_FOUND, List.of(EMPLOYEE_NOT_FOUND))));
    }

    @DeleteMapping("/{employeeId:\\d+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable("employeeId") Long employeeId) {
        log.info("Employee delete requested for id={}", employeeId);
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
        response.setPhotoUrl(employee.getPhotoPath() == null || employee.getPhotoPath().isBlank()
                ? null
                : "/api/employees/" + employee.getEmployeeId() + "/photo");
        response.setActive(employee.getActive());
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());
        return response;
    }

    private Long resolveAuthenticatedEmployeeId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(authentication.getName());
        return employeeId == null ? null : employeeId.longValue();
    }
}
