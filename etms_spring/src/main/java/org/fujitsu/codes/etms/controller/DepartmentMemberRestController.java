package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.DepartmentDao;
import org.fujitsu.codes.etms.model.dao.DeptMembersDao;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.DeptMembers;
import org.fujitsu.codes.etms.model.dto.DeptMembersRequest;
import org.fujitsu.codes.etms.model.dto.DeptMembersResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dept-members")
public class DepartmentMemberRestController {

    private final DeptMembersDao deptMembersDao;
    private final DepartmentDao departmentDao;
    private final EmployeesDao employeesDao;

    public DepartmentMemberRestController(
            DeptMembersDao deptMembersDao,
            DepartmentDao departmentDao,
            EmployeesDao employeesDao) {
        this.deptMembersDao = deptMembersDao;
        this.departmentDao = departmentDao;
        this.employeesDao = employeesDao;
    }

    @GetMapping("/{deptMemberId}")
    public ResponseEntity<?> getById(@PathVariable Long deptMemberId) {
        return deptMembersDao.findById(deptMemberId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Department member not found")));
    }

    @GetMapping
    public ResponseEntity<List<DeptMembersResponse>> getAll() {
        List<DeptMembersResponse> data = deptMembersDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-employee/{employeeNumber}")
    public ResponseEntity<List<DeptMembersResponse>> getByEmployeeNumber(@PathVariable String employeeNumber) {
        List<DeptMembersResponse> data = deptMembersDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-department/{departmentCode}")
    public ResponseEntity<List<DeptMembersResponse>> getByDepartmentCode(@PathVariable String departmentCode) {
        List<DeptMembersResponse> data = deptMembersDao.findByDepartmentCode(departmentCode).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DeptMembersResponse>> search(
            @RequestParam(required = false) String departmentCode,
            @RequestParam(required = false) String employeeNumber) {

        List<DeptMembersResponse> data = deptMembersDao.search(departmentCode, employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody DeptMembersRequest request) {
        normalize(request);

        List<String> errors = validateRequest(request);
        if (!deptMembersDao.search(request.getDepartmentCode(), request.getEmployeeNumber()).isEmpty()) {
            errors.add("Department member already exists");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        DeptMembers entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        DeptMembers saved = deptMembersDao.save(entity);
        return ResponseEntity
                .created(URI.create("/api/dept-members/" + saved.getDeptMemberId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{deptMemberId}")
    public ResponseEntity<?> update(@PathVariable Long deptMemberId, @Valid @RequestBody DeptMembersRequest request) {
        normalize(request);

        var optional = deptMembersDao.findById(deptMemberId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Department member not found"));
        }

        List<String> errors = validateRequest(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        DeptMembers existing = optional.get();
        DeptMembers source = toEntity(request);
        source.setMemberTypeId(existing.getMemberTypeId());
        source.setUpdatedAt(LocalDateTime.now());

        return deptMembersDao.update(deptMemberId, source)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Department member not found")));
    }

    @PatchMapping("/{deptMemberId}/end-membership")
    public ResponseEntity<?> endMembership(@PathVariable Long deptMemberId, @RequestBody Map<String, LocalDate> payload) {
        LocalDate memberEnd = payload.get("memberEnd");

        if (memberEnd == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "memberEnd date is required"));
        }

        var optional = deptMembersDao.findById(deptMemberId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Department member not found"));
        }

        DeptMembers existing = optional.get();

        if (memberEnd.isBefore(existing.getMemberStart())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("errors", List.of("Member end date cannot be before member start date")));
        }

        DeptMembersRequest request = new DeptMembersRequest();
        request.setDepartmentCode(existing.getDepartmentCode());
        request.setEmployeeNumber(existing.getEmployeeNumber());
        request.setMemberStart(existing.getMemberStart());
        request.setMemberEnd(memberEnd);

        DeptMembers source = toEntity(request);
        source.setMemberTypeId(existing.getMemberTypeId());
        source.setUpdatedAt(LocalDateTime.now());

        return deptMembersDao.update(deptMemberId, source)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Department member not found")));
    }

    @DeleteMapping("/{deptMemberId}")
    public ResponseEntity<?> delete(@PathVariable Long deptMemberId) {
        boolean deleted = deptMembersDao.deleteById(deptMemberId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Department member not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validateRequest(DeptMembersRequest request) {
        java.util.ArrayList<String> errors = new java.util.ArrayList<>();

        if (!departmentDao.existsByDepartmentCode(request.getDepartmentCode())) {
            errors.add("Department code does not exist");
        }

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }

        if (request.getMemberEnd() != null && request.getMemberEnd().isBefore(request.getMemberStart())) {
            errors.add("Member end date cannot be before member start date");
        }

        return errors;
    }

    private void normalize(DeptMembersRequest request) {
        if (request.getDepartmentCode() != null) {
            request.setDepartmentCode(request.getDepartmentCode().trim());
        }
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
    }

    private DeptMembers toEntity(DeptMembersRequest request) {
        DeptMembers item = new DeptMembers();
        item.setDepartmentCode(request.getDepartmentCode());
        item.setEmployeeNumber(request.getEmployeeNumber());
        item.setMemberStart(request.getMemberStart());
        item.setMemberEnd(request.getMemberEnd());
        return item;
    }

    private DeptMembersResponse toResponse(DeptMembers item) {
        DeptMembersResponse response = new DeptMembersResponse();
        response.setDeptMemberId(item.getDeptMemberId());
        response.setDepartmentCode(item.getDepartmentCode());
        response.setEmployeeNumber(item.getEmployeeNumber());
        response.setMemberStart(item.getMemberStart());
        response.setMemberEnd(item.getMemberEnd());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }
}
