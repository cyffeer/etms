package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeptMembersResponse {

    private Long deptMemberId;
    private String departmentCode;
    private String employeeNumber;
    private LocalDate memberStart;
    private LocalDate memberEnd;
    private LocalDateTime createdAt;
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

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
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