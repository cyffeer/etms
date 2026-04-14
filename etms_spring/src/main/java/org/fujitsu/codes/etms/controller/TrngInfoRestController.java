package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.TrngInfoDao;
import org.fujitsu.codes.etms.model.dao.TrngTypeDao;
import org.fujitsu.codes.etms.model.dao.VendorInfoDao;
import org.fujitsu.codes.etms.model.data.TrngInfo;
import org.fujitsu.codes.etms.model.dto.TrngInfoRequest;
import org.fujitsu.codes.etms.model.dto.TrngInfoResponse;
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
@RequestMapping("/api/trng-info")
public class TrngInfoRestController {

    private final TrngInfoDao trngInfoDao;
    private final TrngTypeDao trngTypeDao;
    private final VendorInfoDao vendorInfoDao;

    public TrngInfoRestController(TrngInfoDao trngInfoDao, TrngTypeDao trngTypeDao, VendorInfoDao vendorInfoDao) {
        this.trngInfoDao = trngInfoDao;
        this.trngTypeDao = trngTypeDao;
        this.vendorInfoDao = vendorInfoDao;
    }

    @GetMapping("/{trngInfoId}")
    public ResponseEntity<?> getById(@PathVariable Long trngInfoId) {
        return trngInfoDao.findById(trngInfoId)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Training info not found")));
    }

    @GetMapping
    public ResponseEntity<List<TrngInfoResponse>> getAll() {
        List<TrngInfoResponse> data = trngInfoDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TrngInfoRequest request) {
        normalize(request);

        List<String> errors = validate(request, null);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        TrngInfo target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        TrngInfo saved = trngInfoDao.save(target);
        return ResponseEntity.created(URI.create("/api/trng-info/" + saved.getTrngInfoId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{trngInfoId}")
    public ResponseEntity<?> update(@PathVariable Long trngInfoId, @Valid @RequestBody TrngInfoRequest request) {
        normalize(request);

        if (trngInfoDao.findById(trngInfoId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Training info not found"));
        }

        List<String> errors = validate(request, trngInfoId);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        TrngInfo source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return trngInfoDao.update(trngInfoId, source)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Training info not found")));
    }

    @DeleteMapping("/{trngInfoId}")
    public ResponseEntity<?> delete(@PathVariable Long trngInfoId) {
        boolean deleted = trngInfoDao.deleteById(trngInfoId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Training info not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(TrngInfoRequest request, Long trngInfoId) {
        List<String> errors = new ArrayList<>();

        if (trngInfoId == null) {
            if (trngInfoDao.existsByCode(request.getTrngCode())) {
                errors.add("Training code already exists");
            }
        } else {
            if (trngInfoDao.existsByCodeExceptId(request.getTrngCode(), trngInfoId)) {
                errors.add("Training code already exists");
            }
        }

        if (!trngTypeDao.existsByCode(request.getTrngTypeCode())) {
            errors.add("Training type code does not exist");
        }

        if (request.getVendorCode() != null && !request.getVendorCode().isBlank()
                && !vendorInfoDao.existsByCode(request.getVendorCode())) {
            errors.add("Vendor code does not exist");
        }

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            errors.add("End date cannot be before start date");
        }

        return errors;
    }

    private void normalize(TrngInfoRequest request) {
        if (request.getTrngCode() != null) request.setTrngCode(request.getTrngCode().trim());
        if (request.getTrngName() != null) request.setTrngName(request.getTrngName().trim());
        if (request.getTrngTypeCode() != null) request.setTrngTypeCode(request.getTrngTypeCode().trim());
        if (request.getVendorCode() != null) {
            String v = request.getVendorCode().trim();
            request.setVendorCode(v.isEmpty() ? null : v);
        }
        if (request.getLocation() != null) request.setLocation(request.getLocation().trim());
    }

    private TrngInfo toEntity(TrngInfoRequest request) {
        TrngInfo target = new TrngInfo();
        target.setTrngCode(request.getTrngCode());
        target.setTrngName(request.getTrngName());
        target.setTrngTypeCode(request.getTrngTypeCode());
        target.setVendorCode(request.getVendorCode());
        target.setStartDate(request.getStartDate());
        target.setEndDate(request.getEndDate());
        target.setLocation(request.getLocation());
        target.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return target;
    }

    private TrngInfoResponse toResponse(TrngInfo target) {
        TrngInfoResponse response = new TrngInfoResponse();
        response.setTrngInfoId(target.getTrngInfoId());
        response.setTrngCode(target.getTrngCode());
        response.setTrngName(target.getTrngName());
        response.setTrngTypeCode(target.getTrngTypeCode());
        response.setVendorCode(target.getVendorCode());
        response.setStartDate(target.getStartDate());
        response.setEndDate(target.getEndDate());
        response.setLocation(target.getLocation());
        response.setActive(target.getActive());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}