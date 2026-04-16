package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "skills_inventory")
@Access(AccessType.FIELD)
@IdClass(SkillsInventoryId.class)
public class SkillsInventory {

    @Id
    @Column(name = "emp_no", nullable = false)
    private Integer employeeNumber;

    @Id
    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "skills_inventory_id", insertable = false, updatable = false)
    @Generated(event = {EventType.INSERT}, writable = false)
    private Long skillsInventoryId;

    @Column(name = "skill_lvl_id")
    private Long skillLvlId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getSkillsInventoryId() {
        return skillsInventoryId;
    }

    public void setSkillsInventoryId(Long skillsInventoryId) {
        this.skillsInventoryId = skillsInventoryId;
    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
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
