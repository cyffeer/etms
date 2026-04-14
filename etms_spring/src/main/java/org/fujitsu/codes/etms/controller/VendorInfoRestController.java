package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.VendorInfoDao;
import org.fujitsu.codes.etms.model.data.VendorInfo;
import org.fujitsu.codes.etms.model.dto.VendorInfoRequest;
import org.fujitsu.codes.etms.model.dto.VendorInfoResponse;
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
@RequestMapping("/api/vendors")
public class VendorInfoRestController {

    private final VendorInfoDao vendorInfoDao;

    public VendorInfoRestController(VendorInfoDao vendorInfoDao) {
        this.vendorInfoDao = vendorInfoDao;
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<?> getById(@PathVariable Long vendorId) {
        return vendorInfoDao.findById(vendorId)
                .<ResponseEntity<?>>map(v -> ResponseEntity.ok(toResponse(v)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Vendor not found")));
    }

    @GetMapping
    public ResponseEntity<List<VendorInfoResponse>> getAll() {
        List<VendorInfoResponse> data = vendorInfoDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody VendorInfoRequest request) {
        normalize(request);

        if (vendorInfoDao.existsByCode(request.getVendorCode())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Vendor code already exists")));
        }

        VendorInfo target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        VendorInfo saved = vendorInfoDao.save(target);
        return ResponseEntity
                .created(URI.create("/api/vendors/" + saved.getVendorId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{vendorId}")
    public ResponseEntity<?> update(@PathVariable Long vendorId, @Valid @RequestBody VendorInfoRequest request) {
        normalize(request);

        if (vendorInfoDao.findById(vendorId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Vendor not found"));
        }

        if (vendorInfoDao.existsByCodeExceptId(request.getVendorCode(), vendorId)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Vendor code already exists")));
        }

        VendorInfo source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return vendorInfoDao.update(vendorId, source)
                .<ResponseEntity<?>>map(v -> ResponseEntity.ok(toResponse(v)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Vendor not found")));
    }

    @DeleteMapping("/{vendorId}")
    public ResponseEntity<?> delete(@PathVariable Long vendorId) {
        boolean deleted = vendorInfoDao.deleteById(vendorId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Vendor not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(VendorInfoRequest request) {
        if (request.getVendorCode() != null) request.setVendorCode(request.getVendorCode().trim());
        if (request.getVendorName() != null) request.setVendorName(request.getVendorName().trim());
        if (request.getVendorTypeCode() != null) request.setVendorTypeCode(request.getVendorTypeCode().trim());
        if (request.getContactEmail() != null) request.setContactEmail(request.getContactEmail().trim());
        if (request.getContactPhone() != null) request.setContactPhone(request.getContactPhone().trim());
        if (request.getAddressLine() != null) request.setAddressLine(request.getAddressLine().trim());
    }

    private VendorInfo toEntity(VendorInfoRequest request) {
        VendorInfo target = new VendorInfo();
        target.setVendorCode(request.getVendorCode());
        target.setVendorName(request.getVendorName());
        target.setVendorTypeCode(request.getVendorTypeCode());
        target.setContactEmail(request.getContactEmail());
        target.setContactPhone(request.getContactPhone());
        target.setAddressLine(request.getAddressLine());
        target.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return target;
    }

    private VendorInfoResponse toResponse(VendorInfo target) {
        VendorInfoResponse response = new VendorInfoResponse();
        response.setVendorId(target.getVendorId());
        response.setVendorCode(target.getVendorCode());
        response.setVendorName(target.getVendorName());
        response.setVendorTypeCode(target.getVendorTypeCode());
        response.setContactEmail(target.getContactEmail());
        response.setContactPhone(target.getContactPhone());
        response.setAddressLine(target.getAddressLine());
        response.setActive(target.getActive());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}