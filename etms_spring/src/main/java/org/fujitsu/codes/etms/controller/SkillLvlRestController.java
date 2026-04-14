package org.fujitsu.codes.etms.controller;

import org.fujitsu.codes.etms.model.dao.SkillLvlDao;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skill-levels")
public class SkillLvlRestController {

    private final SkillLvlDao skillLvlDao;

    public SkillLvlRestController(SkillLvlDao skillLvlDao) {
        this.skillLvlDao = skillLvlDao;
    }

    @GetMapping("/by-skill/{skillId}")
    public ResponseEntity<ApiResponse<?>> getBySkill(@PathVariable("skillId") Long skillId) {
        var rows = skillLvlDao.findBySkillId(skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill levels fetched", rows));
    }
}
