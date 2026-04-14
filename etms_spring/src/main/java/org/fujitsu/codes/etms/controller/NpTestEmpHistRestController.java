package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.NpTestEmpHistDao;
import org.fujitsu.codes.etms.model.dao.NpTestHistDao;
import org.fujitsu.codes.etms.model.data.NpTestEmpHist;
import org.fujitsu.codes.etms.model.dto.NpTestEmpHistRequest;
import org.fujitsu.codes.etms.model.dto.NpTestEmpHistResponse;
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
@RequestMapping("/api/np-test-emp-hist")
public class NpTestEmpHistRestController {

    private final NpTestEmpHistDao npTestEmpHistDao;
    private final EmployeesDao employeesDao;
    private final NpTestHistDao npTestHistDao;

    public NpTestEmpHistRestController(
            NpTestEmpHistDao npTestEmpHistDao,
            EmployeesDao employeesDao,
            NpTestHistDao npTestHistDao) {
        this.npTestEmpHistDao = npTestEmpHistDao;
        this.employeesDao = employeesDao;
        this.npTestHistDao = npTestHistDao;
    }

    @GetMapping("/{npTestEmpHistId}")
    public ResponseEntity<?> getById(@PathVariable Long npTestEmpHistId) {
        return npTestEmpHistDao.findById(npTestEmpHistId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found")));
    }

    @GetMapping
    public ResponseEntity<List<NpTestEmpHistResponse>> getAll() {
        List<NpTestEmpHistResponse> data = npTestEmpHistDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-employee/{employeeNumber}")
    public ResponseEntity<List<NpTestEmpHistResponse>> getByEmployeeNumber(@PathVariable String employeeNumber) {
        List<NpTestEmpHistResponse> data = npTestEmpHistDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-test/{npTestHistId}")
    public ResponseEntity<List<NpTestEmpHistResponse>> getByTestId(@PathVariable Long npTestHistId) {
        List<NpTestEmpHistResponse> data = npTestEmpHistDao.findByTestId(npTestHistId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    public ResponseEntity<List<NpTestEmpHistResponse>> search(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false, defaultValue = "false") Boolean passedOnly,
            @RequestParam(required = false, defaultValue = "false") Boolean mostRecentOnly) {

        List<NpTestEmpHistResponse> data = npTestEmpHistDao.search(employeeNumber, passedOnly, mostRecentOnly).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody NpTestEmpHistRequest request) {
        normalize(request);

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpTestEmpHist entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        NpTestEmpHist saved = npTestEmpHistDao.save(entity);
        return ResponseEntity.created(URI.create("/api/np-test-emp-hist/" + saved.getNpTestEmpHistId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{npTestEmpHistId}")
    public ResponseEntity<?> update(@PathVariable Long npTestEmpHistId, @Valid @RequestBody NpTestEmpHistRequest request) {
        normalize(request);

        if (npTestEmpHistDao.findById(npTestEmpHistId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found"));
        }

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpTestEmpHist entity = toEntity(request);
        entity.setUpdatedAt(LocalDateTime.now());

        return npTestEmpHistDao.update(npTestEmpHistId, entity)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found")));
    }

    @DeleteMapping("/{npTestEmpHistId}")
    public ResponseEntity<?> delete(@PathVariable Long npTestEmpHistId) {
        boolean deleted = npTestEmpHistDao.deleteById(npTestEmpHistId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(NpTestEmpHistRequest request) {
        List<String> errors = new ArrayList<>();

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }

        if (npTestHistDao.findById(request.getNpTestHistId()).isEmpty()) {
            errors.add("Test id does not exist");
        }

        return errors;
    }

    private void normalize(NpTestEmpHistRequest request) {
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
    }

    private NpTestEmpHist toEntity(NpTestEmpHistRequest request) {
        NpTestEmpHist entity = new NpTestEmpHist();
        entity.setEmployeeNumber(request.getEmployeeNumber());
        entity.setNpTestHistId(request.getNpTestHistId());
        entity.setPassFlag(request.getPassFlag());
        entity.setTakeFlag(request.getTakeFlag());
        entity.setPoints(request.getPoints());
        return entity;
    }

    private NpTestEmpHistResponse toResponse(NpTestEmpHist entity) {
        NpTestEmpHistResponse response = new NpTestEmpHistResponse();
        response.setNpTestEmpHistId(entity.getNpTestEmpHistId());
        response.setEmployeeNumber(entity.getEmployeeNumber());
        response.setNpTestHistId(entity.getNpTestHistId());
        response.setPassFlag(entity.getPassFlag());
        response.setTakeFlag(entity.getTakeFlag());
        response.setPoints(entity.getPoints());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}