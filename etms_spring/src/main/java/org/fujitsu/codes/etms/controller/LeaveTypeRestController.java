package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.LeaveTypeDao;
import org.fujitsu.codes.etms.model.data.LeaveType;
import org.fujitsu.codes.etms.model.dto.LeaveTypeRequest;
import org.fujitsu.codes.etms.model.dto.LeaveTypeResponse;
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
@RequestMapping("/api/leave-types")
public class LeaveTypeRestController {

    private final LeaveTypeDao leaveTypeDao;

    public LeaveTypeRestController(LeaveTypeDao leaveTypeDao) {
        this.leaveTypeDao = leaveTypeDao;
    }

    @GetMapping("/{leaveTypeId}")
    public ResponseEntity<?> getById(@PathVariable Long leaveTypeId) {
        return leaveTypeDao.findById(leaveTypeId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Leave type not found")));
    }

    @GetMapping
    public ResponseEntity<List<LeaveTypeResponse>> getAll(
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<LeaveTypeResponse> data = (keyword == null || keyword.isBlank()
                ? leaveTypeDao.findAll()
                : leaveTypeDao.search(keyword)).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LeaveTypeRequest request) {
        normalize(request);
        if (leaveTypeDao.existsByCode(request.getLeaveTypeCode())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Leave type code already exists")));
        }

        LeaveType target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        LeaveType saved = leaveTypeDao.save(target);
        return ResponseEntity.created(URI.create("/api/leave-types/" + saved.getLeaveTypeId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{leaveTypeId}")
    public ResponseEntity<?> update(@PathVariable Long leaveTypeId, @Valid @RequestBody LeaveTypeRequest request) {
        normalize(request);

        if (leaveTypeDao.findById(leaveTypeId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Leave type not found"));
        }

        if (leaveTypeDao.existsByCodeExceptId(request.getLeaveTypeCode(), leaveTypeId)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Leave type code already exists")));
        }

        LeaveType source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return leaveTypeDao.update(leaveTypeId, source)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Leave type not found")));
    }

    @DeleteMapping("/{leaveTypeId}")
    public ResponseEntity<?> delete(@PathVariable Long leaveTypeId) {
        boolean deleted = leaveTypeDao.deleteById(leaveTypeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Leave type not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(LeaveTypeRequest request) {
        if (request.getLeaveTypeCode() != null) {
            request.setLeaveTypeCode(request.getLeaveTypeCode().trim().toUpperCase());
        }
        if (request.getLeaveTypeName() != null) {
            request.setLeaveTypeName(request.getLeaveTypeName().trim());
        }
        if (request.getDescription() != null) {
            request.setDescription(request.getDescription().trim());
        }
    }

    private LeaveType toEntity(LeaveTypeRequest request) {
        LeaveType target = new LeaveType();
        target.setLeaveTypeCode(request.getLeaveTypeCode());
        target.setLeaveTypeName(request.getLeaveTypeName());
        target.setDescription(request.getDescription());
        target.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return target;
    }

    private LeaveTypeResponse toResponse(LeaveType target) {
        LeaveTypeResponse response = new LeaveTypeResponse();
        response.setLeaveTypeId(target.getLeaveTypeId());
        response.setLeaveTypeCode(target.getLeaveTypeCode());
        response.setLeaveTypeName(target.getLeaveTypeName());
        response.setDescription(target.getDescription());
        response.setActive(target.getActive());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}
