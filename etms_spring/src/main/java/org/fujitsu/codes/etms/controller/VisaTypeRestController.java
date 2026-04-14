package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.VisaTypeDao;
import org.fujitsu.codes.etms.model.data.VisaType;
import org.fujitsu.codes.etms.model.dto.VisaTypeRequest;
import org.fujitsu.codes.etms.model.dto.VisaTypeResponse;
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
@RequestMapping("/api/visa-types")
public class VisaTypeRestController {

    private final VisaTypeDao visaTypeDao;

    public VisaTypeRestController(VisaTypeDao visaTypeDao) {
        this.visaTypeDao = visaTypeDao;
    }

    @GetMapping("/{visaTypeId}")
    public ResponseEntity<?> getById(@PathVariable Long visaTypeId) {
        return visaTypeDao.findById(visaTypeId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Visa type not found")));
    }

    @GetMapping
    public ResponseEntity<List<VisaTypeResponse>> getAll() {
        List<VisaTypeResponse> data = visaTypeDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody VisaTypeRequest request) {
        normalize(request);

        if (visaTypeDao.existsByCode(request.getVisaTypeCode())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Visa type code already exists")));
        }

        VisaType target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        VisaType saved = visaTypeDao.save(target);
        return ResponseEntity.created(URI.create("/api/visa-types/" + saved.getVisaTypeId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{visaTypeId}")
    public ResponseEntity<?> update(@PathVariable Long visaTypeId, @Valid @RequestBody VisaTypeRequest request) {
        normalize(request);

        if (visaTypeDao.findById(visaTypeId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Visa type not found"));
        }

        if (visaTypeDao.existsByCodeExceptId(request.getVisaTypeCode(), visaTypeId)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Visa type code already exists")));
        }

        VisaType source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return visaTypeDao.update(visaTypeId, source)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Visa type not found")));
    }

    @DeleteMapping("/{visaTypeId}")
    public ResponseEntity<?> delete(@PathVariable Long visaTypeId) {
        boolean deleted = visaTypeDao.deleteById(visaTypeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Visa type not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(VisaTypeRequest request) {
        if (request.getVisaTypeCode() != null) {
            request.setVisaTypeCode(request.getVisaTypeCode().trim());
        }
        if (request.getVisaTypeName() != null) {
            request.setVisaTypeName(request.getVisaTypeName().trim());
        }
        if (request.getDescription() != null) {
            request.setDescription(request.getDescription().trim());
        }
    }

    private VisaType toEntity(VisaTypeRequest request) {
        VisaType target = new VisaType();
        target.setVisaTypeCode(request.getVisaTypeCode());
        target.setVisaTypeName(request.getVisaTypeName());
        target.setDescription(request.getDescription());
        target.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return target;
    }

    private VisaTypeResponse toResponse(VisaType target) {
        VisaTypeResponse response = new VisaTypeResponse();
        response.setVisaTypeId(target.getVisaTypeId());
        response.setVisaTypeCode(target.getVisaTypeCode());
        response.setVisaTypeName(target.getVisaTypeName());
        response.setDescription(target.getDescription());
        response.setActive(target.getActive());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}