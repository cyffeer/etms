package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;

public class SkillsInventoryResponse {

    private Long skillsInventoryId;
    private String employeeNumber;
    private Long skillId;
    private Long skillLvlId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getSkillsInventoryId() {
        return skillsInventoryId;
    }

    public void setSkillsInventoryId(Long skillsInventoryId) {
        this.skillsInventoryId = skillsInventoryId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}