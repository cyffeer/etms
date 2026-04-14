package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.VisaInfoDao;
import org.fujitsu.codes.etms.model.dao.VisaTypeDao;
import org.fujitsu.codes.etms.model.data.VisaInfo;
import org.fujitsu.codes.etms.model.dto.VisaInfoRequest;
import org.fujitsu.codes.etms.model.dto.VisaInfoResponse;
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
@RequestMapping("/api/visa-info")
public class VisaInfoRestController {

    private final VisaInfoDao visaInfoDao;
    private final EmployeesDao employeesDao;
    private final VisaTypeDao visaTypeDao;

    public VisaInfoRestController(VisaInfoDao visaInfoDao, EmployeesDao employeesDao, VisaTypeDao visaTypeDao) {
        this.visaInfoDao = visaInfoDao;
        this.employeesDao = employeesDao;
        this.visaTypeDao = visaTypeDao;
    }

    @GetMapping("/{employeeNumber}/{visaTypeId}")
    public ResponseEntity<?> getByKey(
            @PathVariable String employeeNumber,
            @PathVariable Long visaTypeId) {

        return visaInfoDao.findByEmployeeNumberAndVisaTypeId(employeeNumber, visaTypeId)
                .<ResponseEntity<?>>map(v -> ResponseEntity.ok(toResponse(v)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Visa info not found")));
    }

    @GetMapping
    public ResponseEntity<List<VisaInfoResponse>> getAll() {
        List<VisaInfoResponse> data = visaInfoDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-employee/{employeeNumber}")
    public ResponseEntity<List<VisaInfoResponse>> getByEmployee(@PathVariable String employeeNumber) {
        List<VisaInfoResponse> data = visaInfoDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<VisaInfoResponse>> getExpiring(@RequestParam(defaultValue = "30") int days) {
        List<VisaInfoResponse> data = visaInfoDao.findExpiringWithinDays(days).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<VisaInfoResponse>> getExpired() {
        List<VisaInfoResponse> data = visaInfoDao.findExpired().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PatchMapping("/{employeeNumber}/{visaTypeId}/cancel-flag")
    public ResponseEntity<?> updateCancelFlag(
            @PathVariable String employeeNumber,
            @PathVariable Long visaTypeId,
            @RequestBody Map<String, Boolean> payload) {

        return ResponseEntity.badRequest().body(Map.of(
                "message",
                "cancelFlag is not supported by the current schema for visa_info"));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody VisaInfoRequest request) {
        normalize(request);

        List<String> errors = validate(request);
        if (visaInfoDao.findByEmployeeNumberAndVisaTypeId(request.getEmployeeNumber(), request.getVisaTypeId()).isPresent()) {
            errors.add("Visa info already exists");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        VisaInfo target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        VisaInfo saved = visaInfoDao.save(target);
        return ResponseEntity.created(URI.create("/api/visa-info/" + saved.getEmployeeNumber() + "/" + saved.getVisaTypeId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{employeeNumber}/{visaTypeId}")
    public ResponseEntity<?> update(
            @PathVariable String employeeNumber,
            @PathVariable Long visaTypeId,
            @Valid @RequestBody VisaInfoRequest request) {

        normalize(request);

        if (!employeeNumber.equalsIgnoreCase(request.getEmployeeNumber()) || !visaTypeId.equals(request.getVisaTypeId())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Visa key cannot be changed")));
        }

        if (visaInfoDao.findByEmployeeNumberAndVisaTypeId(employeeNumber, visaTypeId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Visa info not found"));
        }

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        VisaInfo source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return visaInfoDao.update(employeeNumber, visaTypeId, source)
                .<ResponseEntity<?>>map(v -> ResponseEntity.ok(toResponse(v)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Visa info not found")));
    }

    @DeleteMapping("/{employeeNumber}/{visaTypeId}")
    public ResponseEntity<?> delete(@PathVariable String employeeNumber, @PathVariable Long visaTypeId) {
        boolean deleted = visaInfoDao.deleteByEmployeeNumberAndVisaTypeId(employeeNumber, visaTypeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Visa info not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(VisaInfoRequest request) {
        List<String> errors = new ArrayList<>();

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }

        if (visaTypeDao.findById(request.getVisaTypeId()).isEmpty()) {
            errors.add("Visa type id does not exist");
        }

        if (request.getIssuedDate() != null && request.getExpiryDate() != null
                && request.getExpiryDate().isBefore(request.getIssuedDate())) {
            errors.add("Expiry date cannot be before issued date");
        }

        return errors;
    }

    private void normalize(VisaInfoRequest request) {
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
    }

    private VisaInfo toEntity(VisaInfoRequest request) {
        VisaInfo v = new VisaInfo();
        v.setEmployeeNumber(request.getEmployeeNumber());
        v.setVisaTypeId(request.getVisaTypeId());
        v.setIssuedDate(request.getIssuedDate());
        v.setExpiryDate(request.getExpiryDate());
        return v;
    }

    private VisaInfoResponse toResponse(VisaInfo v) {
        VisaInfoResponse r = new VisaInfoResponse();
        r.setVisaInfoId(v.getVisaInfoId());
        r.setEmployeeNumber(v.getEmployeeNumber());
        r.setVisaTypeId(v.getVisaTypeId());
        r.setIssuedDate(v.getIssuedDate());
        r.setExpiryDate(v.getExpiryDate());
        r.setCancelFlag(null);
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());
        return r;
    }
}
