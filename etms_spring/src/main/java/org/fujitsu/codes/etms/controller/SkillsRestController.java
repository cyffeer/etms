package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.SkillsDao;
import org.fujitsu.codes.etms.model.data.Skills;
import org.fujitsu.codes.etms.model.dto.SkillsRequest;
import org.fujitsu.codes.etms.model.dto.SkillsResponse;
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
@RequestMapping("/api/skills")
public class SkillsRestController {

    private final SkillsDao skillsDao;

    public SkillsRestController(SkillsDao skillsDao) {
        this.skillsDao = skillsDao;
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<?> getById(@PathVariable Long skillId) {
        return skillsDao.findById(skillId)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(toResponse(s)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Skill not found")));
    }

    @GetMapping
    public ResponseEntity<List<SkillsResponse>> getAll() {
        List<SkillsResponse> data = skillsDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SkillsRequest request) {
        normalize(request);

        if (skillsDao.existsByCode(request.getSkillCode())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Skill code already exists")));
        }

        Skills entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        Skills saved = skillsDao.save(entity);
        return ResponseEntity.created(URI.create("/api/skills/" + saved.getSkillId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<?> update(@PathVariable Long skillId, @Valid @RequestBody SkillsRequest request) {
        normalize(request);

        if (skillsDao.findById(skillId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Skill not found"));
        }

        if (skillsDao.existsByCodeExceptId(request.getSkillCode(), skillId)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Skill code already exists")));
        }

        Skills source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return skillsDao.update(skillId, source)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(toResponse(s)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Skill not found")));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<?> delete(@PathVariable Long skillId) {
        boolean deleted = skillsDao.deleteById(skillId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Skill not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private void normalize(SkillsRequest request) {
        if (request.getSkillCode() != null) {
            request.setSkillCode(request.getSkillCode().trim());
        }
        if (request.getSkillName() != null) {
            request.setSkillName(request.getSkillName().trim());
        }
        if (request.getDescription() != null) {
            request.setDescription(request.getDescription().trim());
        }
    }

    private Skills toEntity(SkillsRequest request) {
        Skills s = new Skills();
        s.setSkillCode(request.getSkillCode());
        s.setSkillName(request.getSkillName());
        s.setDescription(request.getDescription());
        s.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return s;
    }

    private SkillsResponse toResponse(Skills s) {
        SkillsResponse r = new SkillsResponse();
        r.setSkillId(s.getSkillId());
        r.setSkillCode(s.getSkillCode());
        r.setSkillName(s.getSkillName());
        r.setDescription(s.getDescription());
        r.setActive(s.getActive());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }
}