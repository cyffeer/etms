package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.SkillLvlDao;
import org.fujitsu.codes.etms.model.dao.SkillsDao;
import org.fujitsu.codes.etms.model.dao.SkillsInventoryDao;
import org.fujitsu.codes.etms.model.data.SkillsInventory;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.SkillsInventoryRequest;
import org.fujitsu.codes.etms.model.dto.SkillsInventoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/skills-inventory")
public class SkillsInventoryRestController {

    private final SkillsInventoryDao skillsInventoryDao;
    private final EmployeesDao employeesDao;
    private final SkillsDao skillsDao;
    private final SkillLvlDao skillLvlDao;

    private static final org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(SkillsInventoryRestController.class);

    public SkillsInventoryRestController(
            SkillsInventoryDao skillsInventoryDao,
            EmployeesDao employeesDao,
            SkillsDao skillsDao,
            SkillLvlDao skillLvlDao) {
        this.skillsInventoryDao = skillsInventoryDao;
        this.employeesDao = employeesDao;
        this.skillsDao = skillsDao;
        this.skillLvlDao = skillLvlDao;
    }

    @GetMapping("/{skillsInventoryId}")
    public ResponseEntity<ApiResponse<SkillsInventoryResponse>> getById(@PathVariable("skillsInventoryId") Long skillsInventoryId) {
        return skillsInventoryDao.findById(skillsInventoryId)
                .<ResponseEntity<ApiResponse<SkillsInventoryResponse>>>map(row ->
                        ResponseEntity.ok(ApiResponse.success("Skills inventory fetched", toResponse(row))))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error("Record not found", java.util.List.of("Skills inventory not found"))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillsInventoryResponse>>> getAll() {
        try {
            List<SkillsInventory> rows = skillsInventoryDao.findAll();
            List<SkillsInventoryResponse> out = rows.stream()
                    .map(this::toResponse)
                    .toList();

            return ResponseEntity.ok(ApiResponse.success("Skills inventory fetched successfully", out));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(ApiResponse.error(
                    "Internal server error",
                    java.util.List.of(ex.getClass().getName() + ": " + ex.getMessage())));
        }
    }

    // get all skills of an employee
    @GetMapping("/employee/{employeeNumber}/skills")
    public ResponseEntity<List<SkillsInventoryResponse>> getAllSkillsOfEmployee(@PathVariable String employeeNumber) {
        List<SkillsInventoryResponse> data = skillsInventoryDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    // keep existing endpoint for compatibility
    @GetMapping("/by-employee/{employeeNumber}")
    public ResponseEntity<List<SkillsInventoryResponse>> getByEmployee(@PathVariable String employeeNumber) {
        return getAllSkillsOfEmployee(employeeNumber);
    }

    // get employees by skill id
    @GetMapping("/skill/{skillId}/employees")
    public ResponseEntity<List<SkillsInventoryResponse>> getEmployeesBySkillId(@PathVariable Long skillId) {
        List<SkillsInventoryResponse> data = skillsInventoryDao.findBySkillId(skillId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    // assign a skill to an employee
    @PostMapping("/assign")
    public ResponseEntity<?> assignSkill(@Valid @RequestBody SkillsInventoryRequest request) {
        return create(request);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SkillsInventoryRequest request) {
        normalize(request);

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        if (skillsInventoryDao.existsByEmployeeNumberAndSkillId(request.getEmployeeNumber(), request.getSkillId())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Skill is already assigned to employee")));
        }

        SkillsInventory entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        SkillsInventory saved = skillsInventoryDao.save(entity);
        return ResponseEntity.created(URI.create("/api/skills-inventory/" + saved.getSkillsInventoryId()))
                .body(ApiResponse.success("Skills inventory created successfully", toResponse(saved)));
    }

    // update employee skill level
    @PatchMapping("/{skillsInventoryId}/skill-level")
    public ResponseEntity<?> updateEmployeeSkillLevel(
            @PathVariable Long skillsInventoryId,
            @RequestBody Map<String, Long> payload) {

        if (payload == null || payload.get("skillLvlId") == null) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("skillLvlId is required")));
        }

        Long skillLvlId = payload.get("skillLvlId");
        if (skillLvlDao.findById(skillLvlId).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Skill level id does not exist")));
        }

        return skillsInventoryDao.updateSkillLevel(skillsInventoryId, skillLvlId, LocalDateTime.now())
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(ApiResponse.success("Skills inventory updated successfully", toResponse(s))))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Skills inventory record not found")));
    }

    @PutMapping("/{skillsInventoryId}")
    public ResponseEntity<?> update(
            @PathVariable Long skillsInventoryId,
            @Valid @RequestBody SkillsInventoryRequest request) {

        normalize(request);

        if (skillsInventoryDao.findById(skillsInventoryId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Skills inventory record not found"));
        }

        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        SkillsInventory source = toEntity(request);
        source.setUpdatedAt(LocalDateTime.now());

        return skillsInventoryDao.update(skillsInventoryId, source)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(ApiResponse.success("Skills inventory updated successfully", toResponse(s))))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Skills inventory record not found")));
    }

    // delete an employee skill record
    @DeleteMapping("/{skillsInventoryId}")
    public ResponseEntity<?> delete(@PathVariable Long skillsInventoryId) {
        boolean deleted = skillsInventoryDao.deleteById(skillsInventoryId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Skills inventory record not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(SkillsInventoryRequest request) {
        List<String> errors = new ArrayList<>();

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }

        if (skillsDao.findById(request.getSkillId()).isEmpty()) {
            errors.add("Skill id does not exist");
        }

        if (skillLvlDao.findById(request.getSkillLvlId()).isEmpty()) {
            errors.add("Skill level id does not exist");
        }

        return errors;
    }

    private void normalize(SkillsInventoryRequest request) {
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
    }

    private SkillsInventory toEntity(SkillsInventoryRequest request) {
        SkillsInventory s = new SkillsInventory();
        s.setEmployeeNumber(request.getEmployeeNumber());
        s.setSkillId(request.getSkillId());
        s.setSkillLvlId(request.getSkillLvlId());
        return s;
    }

    private SkillsInventoryResponse toResponse(SkillsInventory s) {
        SkillsInventoryResponse r = new SkillsInventoryResponse();
        r.setSkillsInventoryId(s.getSkillsInventoryId() == null ? null : s.getSkillsInventoryId().longValue());
        r.setEmployeeNumber(s.getEmployeeNumber() == null ? null : String.valueOf(s.getEmployeeNumber()));
        r.setSkillId(s.getSkillId() == null ? null : s.getSkillId().longValue());
        r.setSkillLvlId(s.getSkillLvlId() == null ? null : s.getSkillLvlId().longValue());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }
}
