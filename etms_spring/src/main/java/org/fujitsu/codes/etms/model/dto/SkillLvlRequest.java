package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SkillLvlRequest {

    @NotNull(message = "Skill id is required")
    private Long skillId;

    @NotBlank(message = "Level code is required")
    @Size(max = 30, message = "Level code must be at most 30 characters")
    private String lvlCode;

    @NotBlank(message = "Level name is required")
    @Size(max = 100, message = "Level name must be at most 100 characters")
    private String lvlName;

    private Integer lvlRank;
    private Boolean active = Boolean.TRUE;

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getLvlCode() {
        return lvlCode;
    }

    public void setLvlCode(String lvlCode) {
        this.lvlCode = lvlCode;
    }

    public String getLvlName() {
        return lvlName;
    }

    public void setLvlName(String lvlName) {
        this.lvlName = lvlName;
    }

    public Integer getLvlRank() {
        return lvlRank;
    }

    public void setLvlRank(Integer lvlRank) {
        this.lvlRank = lvlRank;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}