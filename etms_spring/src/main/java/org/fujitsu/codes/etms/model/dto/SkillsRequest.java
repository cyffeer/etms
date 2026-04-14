package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SkillsRequest {

    @NotBlank(message = "Skill code is required")
    @Size(max = 50, message = "Skill code must be at most 50 characters")
    private String skillCode;

    @NotBlank(message = "Skill name is required")
    @Size(max = 150, message = "Skill name must be at most 150 characters")
    private String skillName;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    private Boolean active = Boolean.TRUE;

    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}