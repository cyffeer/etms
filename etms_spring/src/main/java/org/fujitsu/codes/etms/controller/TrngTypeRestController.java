package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.TrngTypeDao;
import org.fujitsu.codes.etms.model.data.TrngType;
import org.fujitsu.codes.etms.model.dto.TrngTypeRequest;
import org.fujitsu.codes.etms.model.dto.TrngTypeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/trng-types")
public class TrngTypeRestController {

    private final TrngTypeDao trngTypeDao;

    public TrngTypeRestController(TrngTypeDao trngTypeDao) {
        this.trngTypeDao = trngTypeDao;
    }

    @GetMapping("/{trngTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long trngTypeId) {
        return trngTypeDao.findById(trngTypeId)
                .<ResponseEntity<?>>map(t -> ResponseEntity.ok(toResponse(t)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Training type not found")));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<List<TrngTypeResponse>> getAll() {
        List<TrngTypeResponse> data = trngTypeDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody TrngTypeRequest request) {
        normalize(request);

        TrngType entity = toEntity(request);
        TrngType saved = trngTypeDao.save(entity);
        return ResponseEntity.created(URI.create("/api/trng-types/" + saved.getTrngTypeId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{trngTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long trngTypeId, @Valid @RequestBody TrngTypeRequest request) {
        normalize(request);

        if (trngTypeDao.findById(trngTypeId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Training type not found"));
        }

        TrngType entity = toEntity(request);
        return trngTypeDao.update(trngTypeId, entity)
                .<ResponseEntity<?>>map(t -> ResponseEntity.ok(toResponse(t)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Training type not found")));
    }

    @DeleteMapping("/{trngTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long trngTypeId) {
        boolean deleted = trngTypeDao.deleteById(trngTypeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Training type not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(TrngTypeRequest request) {
        if (request.getTrngTypeNm() != null) {
            request.setTrngTypeNm(request.getTrngTypeNm().trim());
        }
        if (request.getDescription() != null) {
            request.setDescription(request.getDescription().trim());
        }
    }

    private TrngType toEntity(TrngTypeRequest request) {
        TrngType entity = new TrngType();
        entity.setTrngTypeNm(request.getTrngTypeNm());
        entity.setDescription(request.getDescription());
        return entity;
    }

    private TrngTypeResponse toResponse(TrngType entity) {
        TrngTypeResponse response = new TrngTypeResponse();
        response.setTrngTypeId(entity.getTrngTypeId());
        response.setTrngTypeNm(entity.getTrngTypeNm());
        response.setDescription(entity.getDescription());
        return response;
    }
}
