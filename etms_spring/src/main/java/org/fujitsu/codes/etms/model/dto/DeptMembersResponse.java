package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeptMembersResponse {

    private Long deptMemberId;
    private String departmentCode;
    private String departmentName;
    private String employeeNumber;
    private String employeeName;
    private Long memberTypeId;
    private String memberTypeCode;
    private String memberTypeName;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getMemberTypeCode() {
        return memberTypeCode;
    }

    public void setMemberTypeCode(String memberTypeCode) {
        this.memberTypeCode = memberTypeCode;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
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
