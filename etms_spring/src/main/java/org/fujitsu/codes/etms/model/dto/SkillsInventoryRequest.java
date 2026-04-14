package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SkillsInventoryRequest {

    @NotBlank(message = "Employee number is required")
    @Size(max = 30, message = "Employee number must be at most 30 characters")
    private String employeeNumber;

    @NotNull(message = "Skill id is required")
    private Long skillId;

    @NotNull(message = "Skill level id is required")
    private Long skillLvlId;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Long getSkillLvlId() {
        return skillLvlId;
    }

    public void setSkillLvlId(Long skillLvlId) {
        this.skillLvlId = skillLvlId;
    }
}