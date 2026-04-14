package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.fujitsu.codes.etms.model.dao.DepartmentDao;
import org.fujitsu.codes.etms.model.data.Department;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.DepartmentRequest;
import org.fujitsu.codes.etms.model.dto.DepartmentResponse;
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
@RequestMapping("/api/departments")
public class DepartmentRestController {

    private final DepartmentDao departmentDao;

    public DepartmentRestController(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllDepartments() {
        List<DepartmentResponse> data = departmentDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchDepartments(
            @RequestParam(name = "keyword", required = false) String keyword) {

        List<DepartmentResponse> data = departmentDao.search(keyword).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", data));
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
            @PathVariable("departmentId") Long departmentId) {
        Department department = departmentDao.findById(departmentId);
        if (department == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Department not found", List.of("Department not found")));
        }
        return ResponseEntity.ok(ApiResponse.success("Department fetched", toResponse(department)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        normalize(request);

        if (departmentDao.existsByDepartmentCode(request.getDepartmentCode())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Department code already exists", List.of("Department code already exists")));
        }

        Department department = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        department.setCreatedAt(now);
        department.setUpdatedAt(now);

        Department saved = departmentDao.save(department);
        return ResponseEntity
                .created(URI.create("/api/departments/" + saved.getDepartmentId()))
                .body(ApiResponse.success("Department created successfully", toResponse(saved)));
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<?>> updateDepartment(
            @PathVariable("departmentId") Long departmentId,
            @Valid @RequestBody DepartmentRequest request) {
        normalize(request);

        Department existing = departmentDao.findById(departmentId);
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Department not found", List.of("Department not found")));
        }

        if (departmentDao.existsByDepartmentCodeExceptId(request.getDepartmentCode(), departmentId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Department code already exists", List.of("Department code already exists")));
        }

        Department source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        var updated = departmentDao.update(departmentId, source);
        if (updated.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Department updated successfully", toResponse(updated.get())));
        }

        return ResponseEntity.status(404)
                .body(ApiResponse.error("Department not found", List.of("Department not found")));
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @PathVariable("departmentId") Long departmentId) {
        Department d = departmentDao.findById(departmentId);
        if (d == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Department not found", List.of("Department not found")));
        }

        try {
            boolean deleted = departmentDao.deleteById(departmentId);
            if (!deleted) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Department not found", List.of("Department not found")));
            }
            return ResponseEntity.ok(ApiResponse.success("Department deleted", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409)
                    .body(ApiResponse.error("Cannot delete: department is in use", List.of("Department is referenced by other records")));
        }
    }

    private void normalize(DepartmentRequest request) {
        if (request.getDepartmentCode() != null) {
            request.setDepartmentCode(request.getDepartmentCode().trim());
        }
        if (request.getDepartmentName() != null) {
            request.setDepartmentName(request.getDepartmentName().trim());
        }
    }

    private Department toEntity(DepartmentRequest request) {
        Department department = new Department();
        department.setDepartmentCode(request.getDepartmentCode());
        department.setDepartmentName(request.getDepartmentName());
        department.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return department;
    }

    private DepartmentResponse toResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setDepartmentId(department.getDepartmentId());
        response.setDepartmentCode(department.getDepartmentCode());
        response.setDepartmentName(department.getDepartmentName());
        response.setActive(department.getActive());
        response.setCreatedAt(department.getCreatedAt());
        response.setUpdatedAt(department.getUpdatedAt());
        return response;
    }
}
