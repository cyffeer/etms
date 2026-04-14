package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.VendorTypeDao;
import org.fujitsu.codes.etms.model.data.VendorType;
import org.fujitsu.codes.etms.model.dto.VendorTypeRequest;
import org.fujitsu.codes.etms.model.dto.VendorTypeResponse;
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
@RequestMapping("/api/vendor-types")
public class VendorTypeRestController {

    private final VendorTypeDao vendorTypeDao;

    public VendorTypeRestController(VendorTypeDao vendorTypeDao) {
        this.vendorTypeDao = vendorTypeDao;
    }

    @GetMapping("/{vendorTypeId}")
    public ResponseEntity<?> getById(@PathVariable Long vendorTypeId) {
        return vendorTypeDao.findById(vendorTypeId)
                .<ResponseEntity<?>>map(vt -> ResponseEntity.ok(toResponse(vt)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Vendor type not found")));
    }

    @GetMapping
    public ResponseEntity<List<VendorTypeResponse>> getAll() {
        List<VendorTypeResponse> data = vendorTypeDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody VendorTypeRequest request) {
        normalize(request);

        if (vendorTypeDao.existsByCode(request.getVendorTypeCode())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Vendor type code already exists")));
        }

        VendorType target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        VendorType saved = vendorTypeDao.save(target);
        return ResponseEntity
                .created(URI.create("/api/vendor-types/" + saved.getVendorTypeId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{vendorTypeId}")
    public ResponseEntity<?> update(@PathVariable Long vendorTypeId, @Valid @RequestBody VendorTypeRequest request) {
        normalize(request);

        if (vendorTypeDao.findById(vendorTypeId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Vendor type not found"));
        }

        if (vendorTypeDao.existsByCodeExceptId(request.getVendorTypeCode(), vendorTypeId)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Vendor type code already exists")));
        }

        VendorType source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return vendorTypeDao.update(vendorTypeId, source)
                .<ResponseEntity<?>>map(vt -> ResponseEntity.ok(toResponse(vt)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Vendor type not found")));
    }

    @DeleteMapping("/{vendorTypeId}")
    public ResponseEntity<?> delete(@PathVariable Long vendorTypeId) {
        boolean deleted = vendorTypeDao.deleteById(vendorTypeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Vendor type not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(VendorTypeRequest request) {
        if (request.getVendorTypeCode() != null) {
            request.setVendorTypeCode(request.getVendorTypeCode().trim());
        }
        if (request.getVendorTypeName() != null) {
            request.setVendorTypeName(request.getVendorTypeName().trim());
        }
    }

    private VendorType toEntity(VendorTypeRequest request) {
        VendorType target = new VendorType();
        target.setVendorTypeCode(request.getVendorTypeCode());
        target.setVendorTypeName(request.getVendorTypeName());
        target.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return target;
    }

    private VendorTypeResponse toResponse(VendorType target) {
        VendorTypeResponse response = new VendorTypeResponse();
        response.setVendorTypeId(target.getVendorTypeId());
        response.setVendorTypeCode(target.getVendorTypeCode());
        response.setVendorTypeName(target.getVendorTypeName());
        response.setActive(target.getActive());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}
