package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fujitsu.codes.etms.model.dao.SkillLvlDao;
import org.fujitsu.codes.etms.model.dao.SkillsDao;
import org.fujitsu.codes.etms.model.data.SkillLvl;
import org.fujitsu.codes.etms.model.data.Skills;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.SkillLvlRequest;
import org.fujitsu.codes.etms.model.dto.SkillLvlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/skill-levels")
public class SkillLvlRestController {

    private final SkillLvlDao skillLvlDao;
    private final SkillsDao skillsDao;

    public SkillLvlRestController(SkillLvlDao skillLvlDao, SkillsDao skillsDao) {
        this.skillLvlDao = skillLvlDao;
        this.skillsDao = skillsDao;
    }

    @GetMapping("/{skillLvlId}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable("skillLvlId") Long skillLvlId) {
        Map<Long, Skills> skillsById = buildSkillsById();
        return skillLvlDao.findById(skillLvlId)
                .<ResponseEntity<ApiResponse<?>>>map(item ->
                        ResponseEntity.ok(ApiResponse.success("Skill level fetched", toResponse(item, skillsById))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error("Skill level not found", List.of("Skill level not found"))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(name = "skillLvlId", required = false) Long skillLvlId,
            @RequestParam(name = "skillId", required = false) Long skillId,
            @RequestParam(name = "keyword", required = false) String keyword) {
        Map<Long, Skills> skillsById = buildSkillsById();
        var rows = skillLvlDao.search(skillLvlId, skillId, keyword).stream()
                .map(item -> toResponse(item, skillsById))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Skill levels fetched", rows));
    }

    @GetMapping("/by-skill/{skillId}")
    public ResponseEntity<ApiResponse<?>> getBySkill(@PathVariable("skillId") Long skillId) {
        Map<Long, Skills> skillsById = buildSkillsById();
        var rows = skillLvlDao.findBySkillId(skillId).stream()
                .map(item -> toResponse(item, skillsById))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Skill levels fetched", rows));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody SkillLvlRequest request) {
        normalize(request);

        List<String> errors = validateRequest(request, null);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        SkillLvl entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        SkillLvl saved = skillLvlDao.save(entity);
        return ResponseEntity.created(URI.create("/api/skill-levels/" + saved.getSkillLvlId()))
                .body(ApiResponse.success("Skill level created", toResponse(saved, buildSkillsById())));
    }

    @PutMapping("/{skillLvlId}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable("skillLvlId") Long skillLvlId,
            @Valid @RequestBody SkillLvlRequest request) {
        normalize(request);

        if (skillLvlDao.findById(skillLvlId).isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Skill level not found", List.of("Skill level not found")));
        }

        List<String> errors = validateRequest(request, skillLvlId);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errors));
        }

        SkillLvl source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return skillLvlDao.update(skillLvlId, source)
                .<ResponseEntity<ApiResponse<?>>>map(item ->
                        ResponseEntity.ok(ApiResponse.success("Skill level updated", toResponse(item, buildSkillsById()))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error("Skill level not found", List.of("Skill level not found"))));
    }

    @DeleteMapping("/{skillLvlId}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("skillLvlId") Long skillLvlId) {
        boolean deleted = skillLvlDao.deleteById(skillLvlId);
        if (!deleted) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Skill level not found", List.of("Skill level not found")));
        }
        return ResponseEntity.ok(ApiResponse.success("Skill level deleted", null));
    }

    private void normalize(SkillLvlRequest request) {
        if (request.getLvlCode() != null) {
            request.setLvlCode(request.getLvlCode().trim());
        }
        if (request.getLvlName() != null) {
            request.setLvlName(request.getLvlName().trim());
        }
    }

    private List<String> validateRequest(SkillLvlRequest request, Long skillLvlId) {
        var errors = new java.util.ArrayList<String>();

        if (skillsDao.findById(request.getSkillId()).isEmpty()) {
            errors.add("Skill does not exist");
        }

        boolean duplicate = skillLvlId == null
                ? skillLvlDao.existsBySkillIdAndLvlCode(request.getSkillId(), request.getLvlCode())
                : skillLvlDao.existsBySkillIdAndLvlCodeExceptId(request.getSkillId(), request.getLvlCode(), skillLvlId);
        if (duplicate) {
            errors.add("Level code already exists for the selected skill");
        }

        return errors;
    }

    private SkillLvl toEntity(SkillLvlRequest request) {
        SkillLvl item = new SkillLvl();
        item.setSkillId(request.getSkillId());
        item.setLvlCode(request.getLvlCode());
        item.setLvlName(request.getLvlName());
        item.setLvlRank(request.getLvlRank());
        item.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return item;
    }

    private SkillLvlResponse toResponse(SkillLvl item, Map<Long, Skills> skillsById) {
        SkillLvlResponse response = new SkillLvlResponse();
        response.setSkillLvlId(item.getSkillLvlId());
        response.setSkillId(item.getSkillId());
        response.setLvlCode(item.getLvlCode());
        response.setLvlName(item.getLvlName());
        response.setLvlRank(item.getLvlRank());
        response.setActive(item.getActive());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        Skills skill = skillsById.get(item.getSkillId());
        if (skill != null) {
            response.setSkillCode(skill.getSkillCode());
            response.setSkillName(skill.getSkillName());
        }
        return response;
    }

    private Map<Long, Skills> buildSkillsById() {
        return skillsDao.findAll().stream()
                .collect(Collectors.toMap(Skills::getSkillId, Function.identity()));
    }
}
