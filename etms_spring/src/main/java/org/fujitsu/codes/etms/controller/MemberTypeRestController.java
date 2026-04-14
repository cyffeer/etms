package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.MemberTypeDao;
import org.fujitsu.codes.etms.model.data.MemberType;
import org.fujitsu.codes.etms.model.dto.MemberTypeRequest;
import org.fujitsu.codes.etms.model.dto.MemberTypeResponse;
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
@RequestMapping("/api/member-types")
public class MemberTypeRestController {

    private final MemberTypeDao memberTypeDao;

    public MemberTypeRestController(MemberTypeDao memberTypeDao) {
        this.memberTypeDao = memberTypeDao;
    }

    @GetMapping("/{memberTypeId}")
    public ResponseEntity<?> getById(@PathVariable Long memberTypeId) {
        return memberTypeDao.findById(memberTypeId)
                .<ResponseEntity<?>>map(mt -> ResponseEntity.ok(toResponse(mt)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Member type not found")));
    }

    @GetMapping
    public ResponseEntity<List<MemberTypeResponse>> getAll() {
        List<MemberTypeResponse> data = memberTypeDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MemberTypeRequest request) {
        normalize(request);

        if (memberTypeDao.existsByCode(request.getMemberTypeCode())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Member type code already exists")));
        }

        MemberType target = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        target.setCreatedAt(now);
        target.setUpdatedAt(now);

        MemberType saved = memberTypeDao.save(target);
        return ResponseEntity
                .created(URI.create("/api/member-types/" + saved.getMemberTypeId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{memberTypeId}")
    public ResponseEntity<?> update(@PathVariable Long memberTypeId, @Valid @RequestBody MemberTypeRequest request) {
        normalize(request);

        if (memberTypeDao.findById(memberTypeId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Member type not found"));
        }

        if (memberTypeDao.existsByCodeExceptId(request.getMemberTypeCode(), memberTypeId)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Member type code already exists")));
        }

        MemberType source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return memberTypeDao.update(memberTypeId, source)
                .<ResponseEntity<?>>map(mt -> ResponseEntity.ok(toResponse(mt)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Member type not found")));
    }

    @DeleteMapping("/{memberTypeId}")
    public ResponseEntity<?> delete(@PathVariable Long memberTypeId) {
        boolean deleted = memberTypeDao.deleteById(memberTypeId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Member type not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(MemberTypeRequest request) {
        if (request.getMemberTypeCode() != null) {
            request.setMemberTypeCode(request.getMemberTypeCode().trim());
        }
        if (request.getMemberTypeName() != null) {
            request.setMemberTypeName(request.getMemberTypeName().trim());
        }
    }

    private MemberType toEntity(MemberTypeRequest request) {
        MemberType target = new MemberType();
        target.setMemberTypeCode(request.getMemberTypeCode());
        target.setMemberTypeName(request.getMemberTypeName());
        target.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return target;
    }

    private MemberTypeResponse toResponse(MemberType target) {
        MemberTypeResponse response = new MemberTypeResponse();
        response.setMemberTypeId(target.getMemberTypeId());
        response.setMemberTypeCode(target.getMemberTypeCode());
        response.setMemberTypeName(target.getMemberTypeName());
        response.setActive(target.getActive());
        response.setCreatedAt(target.getCreatedAt());
        response.setUpdatedAt(target.getUpdatedAt());
        return response;
    }
}