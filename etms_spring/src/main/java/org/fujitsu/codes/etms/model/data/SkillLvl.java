package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "skill_lvl")
public class SkillLvl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_lvl_id")
    private Long skillLvlId;

    @Column(name = "skill_lvl_nm", length = 25)
    private String lvlName;

    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "lvl_code")
    private String lvlCode;

    @Column(name = "lvl_rank")
    private Integer lvlRank;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getSkillLvlId() {
        return skillLvlId;
    }

    public void setSkillLvlId(Long skillLvlId) {
        this.skillLvlId = skillLvlId;
    }

    public String getLvlName() {
        return lvlName;
    }

    public void setLvlName(String lvlName) {
        this.lvlName = lvlName;
    }

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

    public String getSkillLvlName() {
        return this.lvlName;
    }

    public Integer getLvlRank() {
        return lvlRank;
    }

    public void setLvlRank(Integer lvlRank) {
        this.lvlRank = lvlRank;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
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
