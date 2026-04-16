package org.fujitsu.codes.etms.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "dept_members")
@IdClass(DeptMembersId.class)
public class DeptMembers {

    @Id
    @Column(name = "dept_code", nullable = false, length = 4)
    private String departmentCode;

    @Id
    @Column(name = "emp_no", nullable = false)
    private Integer employeeNumber;

    @Column(name = "dept_member_id", insertable = false, updatable = false)
    @Generated(event = {EventType.INSERT}, writable = false)
    private Long deptMemberId;

    @Column(name = "mbr_type_id")
    private Long memberTypeId;

    @Column(name = "member_start")
    private LocalDate memberStart;

    @Column(name = "member_end")
    private LocalDate memberEnd;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getDeptMemberId() {
        return deptMemberId;
    }

    public void setDeptMemberId(Long deptMemberId) {
        this.deptMemberId = deptMemberId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public LocalDate getMemberStart() {
        return memberStart;
    }

    public void setMemberStart(LocalDate memberStart) {
        this.memberStart = memberStart;
    }

    public LocalDate getMemberEnd() {
        return memberEnd;
    }

    public void setMemberEnd(LocalDate memberEnd) {
        this.memberEnd = memberEnd;
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
