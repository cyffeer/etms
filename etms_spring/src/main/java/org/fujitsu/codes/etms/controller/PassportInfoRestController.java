package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.PassportInfoDao;
import org.fujitsu.codes.etms.model.data.PassportInfo;
import org.fujitsu.codes.etms.model.dto.PassportInfoRequest;
import org.fujitsu.codes.etms.model.dto.PassportInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/passport-info")
public class PassportInfoRestController {

    private final PassportInfoDao passportInfoDao;
    private final EmployeesDao employeesDao;

    public PassportInfoRestController(PassportInfoDao passportInfoDao, EmployeesDao employeesDao) {
        this.passportInfoDao = passportInfoDao;
        this.employeesDao = employeesDao;
    }

    @GetMapping("/{passportNumber}")
    public ResponseEntity<?> getByPassportNumber(@PathVariable String passportNumber) {
        return passportInfoDao.findByPassportNumber(passportNumber)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Passport info not found")));
    }

    @GetMapping
    public ResponseEntity<List<PassportInfoResponse>> getAll() {
        List<PassportInfoResponse> data = passportInfoDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-employee/{employeeNumber}")
    public ResponseEntity<List<PassportInfoResponse>> getByEmployee(@PathVariable String employeeNumber) {
        List<PassportInfoResponse> data = passportInfoDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-employee/{employeeNumber}/latest")
    public ResponseEntity<?> getLatestByEmployee(@PathVariable String employeeNumber) {
        return passportInfoDao.findLatestByEmployeeNumber(employeeNumber)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Passport info not found")));
    }

    @GetMapping("/expired")
    public ResponseEntity<List<PassportInfoResponse>> getExpired() {
        List<PassportInfoResponse> data = passportInfoDao.findExpired().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PassportInfoRequest request) {
        normalize(request);

        List<String> errors = validate(request, null);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        PassportInfo entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        PassportInfo saved = passportInfoDao.save(entity);
        return ResponseEntity.created(URI.create("/api/passport-info/" + saved.getPassportNumber()))
                .body(toResponse(saved));
    }

    @PutMapping("/{passportNumber}")
    public ResponseEntity<?> update(
            @PathVariable String passportNumber,
            @Valid @RequestBody PassportInfoRequest request) {

        normalize(request);

        if (passportInfoDao.findByPassportNumber(passportNumber).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Passport info not found"));
        }

        List<String> errors = validate(request, passportNumber);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        PassportInfo source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return passportInfoDao.update(passportNumber, source)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Passport info not found")));
    }

    @DeleteMapping("/{passportNumber}")
    public ResponseEntity<?> delete(@PathVariable String passportNumber) {
        boolean deleted = passportInfoDao.deleteByPassportNumber(passportNumber);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Passport info not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(PassportInfoRequest request, String currentPassportNumber) {
        List<String> errors = new ArrayList<>();

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }

        if (currentPassportNumber == null) {
            if (passportInfoDao.existsByPassportNumber(request.getPassportNumber())) {
                errors.add("Passport number already exists");
            }
        } else if (!currentPassportNumber.equalsIgnoreCase(request.getPassportNumber())) {
            errors.add("Passport number cannot be changed");
        }

        if (request.getIssuedDate() != null && request.getExpiryDate() != null
                && request.getExpiryDate().isBefore(request.getIssuedDate())) {
            errors.add("Expiry date cannot be before issued date");
        }

        return errors;
    }

    private void normalize(PassportInfoRequest request) {
        if (request.getPassportNumber() != null) {
            request.setPassportNumber(request.getPassportNumber().trim());
        }
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
    }

    private PassportInfo toEntity(PassportInfoRequest request) {
        PassportInfo p = new PassportInfo();
        p.setPassportNumber(request.getPassportNumber());
        p.setEmployeeNumber(request.getEmployeeNumber());
        p.setIssuedDate(request.getIssuedDate());
        p.setExpiryDate(request.getExpiryDate());
        return p;
    }

    private PassportInfoResponse toResponse(PassportInfo p) {
        PassportInfoResponse r = new PassportInfoResponse();
        r.setPassportInfoId(p.getPassportInfoId());
        r.setPassportNumber(p.getPassportNumber());
        r.setEmployeeNumber(p.getEmployeeNumber());
        r.setIssuedDate(p.getIssuedDate());
        r.setExpiryDate(p.getExpiryDate());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }
}
