package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.NpLvlInfoDao;
import org.fujitsu.codes.etms.model.dao.NpTypeDao;
import org.fujitsu.codes.etms.model.data.NpLvlInfo;
import org.fujitsu.codes.etms.model.dto.NpLvlInfoRequest;
import org.fujitsu.codes.etms.model.dto.NpLvlInfoResponse;
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
@RequestMapping("/api/np-lvl-info")
public class NpLvlInfoRestController {

    private final NpLvlInfoDao npLvlInfoDao;
    private final NpTypeDao npTypeDao;

    public NpLvlInfoRestController(NpLvlInfoDao npLvlInfoDao, NpTypeDao npTypeDao) {
        this.npLvlInfoDao = npLvlInfoDao;
        this.npTypeDao = npTypeDao;
    }

    @GetMapping("/{npLvlInfoId}")
    public ResponseEntity<?> getById(@PathVariable Long npLvlInfoId) {
        return npLvlInfoDao.findById(npLvlInfoId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP level info not found")));
    }

    @GetMapping
    public ResponseEntity<List<NpLvlInfoResponse>> getAll() {
        List<NpLvlInfoResponse> data = npLvlInfoDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody NpLvlInfoRequest request) {
        normalize(request);

        List<String> errors = validate(request, null);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpLvlInfo entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        NpLvlInfo saved = npLvlInfoDao.save(entity);
        return ResponseEntity.created(URI.create("/api/np-lvl-info/" + saved.getNpLvlInfoId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{npLvlInfoId}")
    public ResponseEntity<?> update(@PathVariable Long npLvlInfoId, @Valid @RequestBody NpLvlInfoRequest request) {
        normalize(request);

        if (npLvlInfoDao.findById(npLvlInfoId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "NP level info not found"));
        }

        List<String> errors = validate(request, npLvlInfoId);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpLvlInfo entity = toEntity(request);
        entity.setUpdatedAt(LocalDateTime.now());

        return npLvlInfoDao.update(npLvlInfoId, entity)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP level info not found")));
    }

    @DeleteMapping("/{npLvlInfoId}")
    public ResponseEntity<?> delete(@PathVariable Long npLvlInfoId) {
        boolean deleted = npLvlInfoDao.deleteById(npLvlInfoId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "NP level info not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(NpLvlInfoRequest request, Long npLvlInfoId) {
        List<String> errors = new ArrayList<>();

        if (npLvlInfoId == null) {
            if (npLvlInfoDao.existsByCode(request.getNpLvlInfoCode())) {
                errors.add("NP level info code already exists");
            }
        } else {
            if (npLvlInfoDao.existsByCodeExceptId(request.getNpLvlInfoCode(), npLvlInfoId)) {
                errors.add("NP level info code already exists");
            }
        }

        if (!npTypeDao.existsByCode(request.getNpTypeCode())) {
            errors.add("NP type code does not exist");
        }

        if (request.getValidFrom() != null && request.getValidTo() != null
                && request.getValidTo().isBefore(request.getValidFrom())) {
            errors.add("Valid to date cannot be before valid from date");
        }

        return errors;
    }

    private void normalize(NpLvlInfoRequest request) {
        if (request.getNpLvlInfoCode() != null) {
            request.setNpLvlInfoCode(request.getNpLvlInfoCode().trim());
        }
        if (request.getNpLvlInfoName() != null) {
            request.setNpLvlInfoName(request.getNpLvlInfoName().trim());
        }
        if (request.getNpTypeCode() != null) {
            request.setNpTypeCode(request.getNpTypeCode().trim());
        }
        if (request.getAllowanceCurrency() != null) {
            request.setAllowanceCurrency(request.getAllowanceCurrency().trim());
        }
    }

    private NpLvlInfo toEntity(NpLvlInfoRequest request) {
        NpLvlInfo entity = new NpLvlInfo();
        entity.setNpLvlInfoCode(request.getNpLvlInfoCode());
        entity.setNpLvlInfoName(request.getNpLvlInfoName());
        entity.setNpTypeCode(request.getNpTypeCode());
        entity.setValidFrom(request.getValidFrom());
        entity.setValidTo(request.getValidTo());
        entity.setAllowanceAmount(request.getAllowanceAmount());
        entity.setAllowanceCurrency(request.getAllowanceCurrency());
        entity.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return entity;
    }

    private NpLvlInfoResponse toResponse(NpLvlInfo entity) {
        NpLvlInfoResponse response = new NpLvlInfoResponse();
        response.setNpLvlInfoId(entity.getNpLvlInfoId());
        response.setNpLvlInfoCode(entity.getNpLvlInfoCode());
        response.setNpLvlInfoName(entity.getNpLvlInfoName());
        response.setNpTypeCode(entity.getNpTypeCode());
        response.setValidFrom(entity.getValidFrom());
        response.setValidTo(entity.getValidTo());
        response.setAllowanceAmount(entity.getAllowanceAmount());
        response.setAllowanceCurrency(entity.getAllowanceCurrency());
        response.setActive(entity.getActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}