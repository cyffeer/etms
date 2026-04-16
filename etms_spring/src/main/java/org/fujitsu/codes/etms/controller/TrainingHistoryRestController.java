package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.TrngHistDao;
import org.fujitsu.codes.etms.model.dao.TrngInfoDao;
import org.fujitsu.codes.etms.model.data.TrngHist;
import org.fujitsu.codes.etms.model.dto.TrngHistRequest;
import org.fujitsu.codes.etms.model.dto.TrngHistResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trng-history")
public class TrainingHistoryRestController {

    private final TrngHistDao trngHistDao;
    private final EmployeesDao employeesDao;
    private final TrngInfoDao trngInfoDao;

    public TrainingHistoryRestController(TrngHistDao trngHistDao, EmployeesDao employeesDao, TrngInfoDao trngInfoDao) {
        this.trngHistDao = trngHistDao;
        this.employeesDao = employeesDao;
        this.trngInfoDao = trngInfoDao;
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> assignTrainingToEmployee(@Valid @RequestBody TrngHistRequest request) {
        normalize(request);

        List<String> errors = validate(request);
        if (trngHistDao.findByTrainingAndEmployee(request.getTrngId(), request.getEmployeeNumber()).isPresent()) {
            errors.add("Training history already exists");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        TrngHist target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        TrngHist saved = trngHistDao.save(target);
        return ResponseEntity
                .created(URI.create("/api/trng-history/" + saved.getTrngHistId() + "/" + saved.getEmployeeNumber()))
                .body(toResponse(saved));
    }

    @GetMapping("/{trngId}/{employeeNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> getByKey(
            @PathVariable Long trngId,
            @PathVariable String employeeNumber) {

        return trngHistDao.findByTrainingAndEmployee(trngId, employeeNumber)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Training history not found")));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> getAllTrainingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "page must be >= 0 and size must be > 0"));
        }

        List<TrngHistResponse> data = trngHistDao.findAll(page, size).stream()
                .map(this::toResponse)
                .toList();

        long totalElements = trngHistDao.countAll();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Training history fetched successfully",
                "data", data,
                "page", page,
                "size", size,
                "totalElements", totalElements,
                "totalPages", totalPages
        ));
    }

    @GetMapping("/by-employee/{employeeNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<List<TrngHistResponse>> getByEmployeeNumber(@PathVariable String employeeNumber) {
        List<TrngHistResponse> data = trngHistDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-training/{trainingId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<List<TrngHistResponse>> getByTrainingId(@PathVariable Long trainingId) {
        List<TrngHistResponse> data = trngHistDao.findByTrainingId(trainingId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{trngId}/{employeeNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> deleteTrainingHistory(@PathVariable Long trngId, @PathVariable String employeeNumber) {
        boolean deleted = trngHistDao.deleteByTrainingAndEmployee(trngId, employeeNumber);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Training history not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(TrngHistRequest request) {
        List<String> errors = new ArrayList<>();

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }

        if (trngInfoDao.findById(request.getTrngId()).isEmpty()) {
            errors.add("Training id does not exist");
        }

        return errors;
    }

    private void normalize(TrngHistRequest request) {
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
    }

    private TrngHist toEntity(TrngHistRequest request) {
        TrngHist target = new TrngHist();
        target.setTrngHistId(request.getTrngId());
        target.setEmployeeNumber(request.getEmployeeNumber());
        return target;
    }

    private TrngHistResponse toResponse(TrngHist target) {
        TrngHistResponse response = new TrngHistResponse();
        response.setTrngId(target.getTrngHistId());
        response.setEmployeeNumber(target.getEmployeeNumber());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}
