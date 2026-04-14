package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.NpLvlInfoDao;
import org.fujitsu.codes.etms.model.dao.NpTestHistDao;
import org.fujitsu.codes.etms.model.data.NpTestHist;
import org.fujitsu.codes.etms.model.dto.NpTestHistRequest;
import org.fujitsu.codes.etms.model.dto.NpTestHistResponse;
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
@RequestMapping("/api/np-test-hist")
public class NpTestHistRestController {

    private final NpTestHistDao npTestHistDao;
    private final NpLvlInfoDao npLvlInfoDao;

    public NpTestHistRestController(NpTestHistDao npTestHistDao, NpLvlInfoDao npLvlInfoDao) {
        this.npTestHistDao = npTestHistDao;
        this.npLvlInfoDao = npLvlInfoDao;
    }

    @GetMapping("/{npTestHistId}")
    public ResponseEntity<?> getById(@PathVariable Long npTestHistId) {
        return npTestHistDao.findById(npTestHistId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP test history not found")));
    }

    @GetMapping
    public ResponseEntity<List<NpTestHistResponse>> getAll() {
        List<NpTestHistResponse> data = npTestHistDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody NpTestHistRequest request) {
        normalize(request);

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpTestHist entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        NpTestHist saved = npTestHistDao.save(entity);
        return ResponseEntity.created(URI.create("/api/np-test-hist/" + saved.getNpTestHistId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{npTestHistId}")
    public ResponseEntity<?> update(@PathVariable Long npTestHistId, @Valid @RequestBody NpTestHistRequest request) {
        normalize(request);

        if (npTestHistDao.findById(npTestHistId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "NP test history not found"));
        }

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpTestHist entity = toEntity(request);
        entity.setUpdatedAt(LocalDateTime.now());

        return npTestHistDao.update(npTestHistId, entity)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP test history not found")));
    }

    @DeleteMapping("/{npTestHistId}")
    public ResponseEntity<?> delete(@PathVariable Long npTestHistId) {
        boolean deleted = npTestHistDao.deleteById(npTestHistId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "NP test history not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(NpTestHistRequest request) {
        List<String> errors = new ArrayList<>();

        if (!npLvlInfoDao.existsByCode(request.getNpLvlInfoCode())) {
            errors.add("NP level info code does not exist");
        }

        return errors;
    }

    private void normalize(NpTestHistRequest request) {
        if (request.getNpLvlInfoCode() != null) {
            request.setNpLvlInfoCode(request.getNpLvlInfoCode().trim());
        }
        if (request.getTestCenter() != null) {
            request.setTestCenter(request.getTestCenter().trim());
        }
        if (request.getTestLevel() != null) {
            request.setTestLevel(request.getTestLevel().trim());
        }
        if (request.getRemarks() != null) {
            request.setRemarks(request.getRemarks().trim());
        }
    }

    private NpTestHist toEntity(NpTestHistRequest request) {
        NpTestHist entity = new NpTestHist();
        entity.setNpLvlInfoCode(request.getNpLvlInfoCode());
        entity.setTestDate(request.getTestDate());
        entity.setTestCenter(request.getTestCenter());
        entity.setTestLevel(request.getTestLevel());
        entity.setScore(request.getScore());
        entity.setPassed(request.getPassed());
        entity.setRemarks(request.getRemarks());
        return entity;
    }

    private NpTestHistResponse toResponse(NpTestHist entity) {
        NpTestHistResponse response = new NpTestHistResponse();
        response.setNpTestHistId(entity.getNpTestHistId());
        response.setNpLvlInfoCode(entity.getNpLvlInfoCode());
        response.setTestDate(entity.getTestDate());
        response.setTestCenter(entity.getTestCenter());
        response.setTestLevel(entity.getTestLevel());
        response.setScore(entity.getScore());
        response.setPassed(entity.getPassed());
        response.setRemarks(entity.getRemarks());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}