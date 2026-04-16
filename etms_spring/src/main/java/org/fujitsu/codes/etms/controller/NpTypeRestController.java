package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.NpTypeDao;
import org.fujitsu.codes.etms.model.data.NpType;
import org.fujitsu.codes.etms.model.dto.NpTypeRequest;
import org.fujitsu.codes.etms.model.dto.NpTypeResponse;
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
@RequestMapping("/api/np-types")
public class NpTypeRestController {

    private final NpTypeDao npTypeDao;

    public NpTypeRestController(NpTypeDao npTypeDao) {
        this.npTypeDao = npTypeDao;
    }

    @GetMapping("/{npTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long npTypeId) {
        return npTypeDao.findById(npTypeId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP type not found")));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<List<NpTypeResponse>> getAll() {
        List<NpTypeResponse> data = npTypeDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> create(@Valid @RequestBody NpTypeRequest request) {
        normalize(request);

        if (npTypeDao.existsByCode(request.getNpTypeCode())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("NP type code already exists")));
        }

        NpType target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        NpType saved = npTypeDao.save(target);
        return ResponseEntity
                .created(URI.create("/api/np-types/" + saved.getNpTypeId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{npTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> update(@PathVariable Long npTypeId, @Valid @RequestBody NpTypeRequest request) {
        normalize(request);

        if (npTypeDao.findById(npTypeId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "NP type not found"));
        }

        if (npTypeDao.existsByCodeExceptId(request.getNpTypeCode(), npTypeId)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("NP type code already exists")));
        }

        NpType source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return npTypeDao.update(npTypeId, source)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP type not found")));
    }

    @DeleteMapping("/{npTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> delete(@PathVariable Long npTypeId) {
        boolean deleted = npTypeDao.deleteById(npTypeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "NP type not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(NpTypeRequest request) {
        if (request.getNpTypeCode() != null) {
            request.setNpTypeCode(request.getNpTypeCode().trim());
        }
        if (request.getNpTypeName() != null) {
            request.setNpTypeName(request.getNpTypeName().trim());
        }
    }

    private NpType toEntity(NpTypeRequest request) {
        NpType target = new NpType();
        target.setNpTypeCode(request.getNpTypeCode());
        target.setNpTypeName(request.getNpTypeName());
        target.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return target;
    }

    private NpTypeResponse toResponse(NpType target) {
        NpTypeResponse response = new NpTypeResponse();
        response.setNpTypeId(target.getNpTypeId());
        response.setNpTypeCode(target.getNpTypeCode());
        response.setNpTypeName(target.getNpTypeName());
        response.setActive(target.getActive());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}
