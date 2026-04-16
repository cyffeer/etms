package org.fujitsu.codes.etms.model.data;

import java.io.Serializable;
import java.util.Objects;

public class SkillsInventoryId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer employeeNumber;
    private Long skillId;

    public SkillsInventoryId() {
    }

    public SkillsInventoryId(Integer employeeNumber, Long skillId) {
        this.employeeNumber = employeeNumber;
        this.skillId = skillId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillsInventoryId that)) return false;
        return Objects.equals(employeeNumber, that.employeeNumber)
                && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNumber, skillId);
    }
}
