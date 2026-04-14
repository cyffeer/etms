package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.util.List;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.dao.LeaveTypeDao;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.fujitsu.codes.etms.model.data.LeaveType;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.LeaveRequest;
import org.fujitsu.codes.etms.model.dto.LeaveResponse;
import org.fujitsu.codes.etms.validator.LeaveValidator;
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

    private final LeaveDao leaveDao;
    private final EmployeesDao employeesDao;
    private final LeaveTypeDao leaveTypeDao;

    public LeaveRestController(LeaveDao leaveDao, EmployeesDao employeesDao, LeaveTypeDao leaveTypeDao) {
        this.leaveDao = leaveDao;
        this.employeesDao = employeesDao;
        this.leaveTypeDao = leaveTypeDao;
    }

    @GetMapping("/{leaveRecordId}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable("leaveRecordId") Long leaveRecordId) {
        return leaveDao.findById(leaveRecordId)
                .<ResponseEntity<ApiResponse<?>>>map(l ->
                        ResponseEntity.ok(ApiResponse.success("Leave fetched successfully", toResponse(l))))
                .orElseGet(() ->
                        ResponseEntity.status(404).body(ApiResponse.error("Leave not found", List.of("Leave not found"))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<LeaveResponse> data = leaveDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", data));
    }

    @GetMapping("/employee/{employeeNumber}")
    public ResponseEntity<ApiResponse<?>> getByEmployee(@PathVariable("employeeNumber") String employeeNumber) {
        List<LeaveResponse> data = leaveDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", data));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<?>> getByStatus(@PathVariable("status") String status) {
        List<LeaveResponse> data = leaveDao.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> search(
            @RequestParam(name = "employeeNumber", required = false) String employeeNumber,
            @RequestParam(name = "leaveType", required = false) String leaveType,
            @RequestParam(name = "status", required = false) String status) {

        List<LeaveResponse> data = leaveDao.search(employeeNumber, leaveType, status).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Leaves fetched successfully", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody LeaveRequest request) {
        List<String> errors = LeaveValidator.validate(request);
        if (!errors.isEmpty()) {
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
        return ResponseEntity.created(URI.create("/api/leaves/" + saved.getLeaveRecordId()))
                .body(ApiResponse.success("Leave created successfully", toResponse(saved)));
    }

    @PutMapping("/{leaveRecordId}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable("leaveRecordId") Long leaveRecordId,
            @Valid @RequestBody LeaveRequest request) {

        if (leaveDao.findById(leaveRecordId).isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponse.error("Leave not found", List.of("Leave not found")));
        }

        List<String> errors = LeaveValidator.validate(request);
        if (!errors.isEmpty()) {
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
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("leaveRecordId") Long leaveRecordId) {
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
}
