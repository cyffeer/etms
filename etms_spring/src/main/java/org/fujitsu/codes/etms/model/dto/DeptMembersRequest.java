package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DeptMembersRequest {

    @NotBlank(message = "Department code is required")
    @Size(max = 30, message = "Department code must be at most 30 characters")
    private String departmentCode;

    @NotBlank(message = "Employee number is required")
    @Size(max = 30, message = "Employee number must be at most 30 characters")
    private String employeeNumber;

    private Long memberTypeId;

    @NotNull(message = "Member start date is required")
    private LocalDate memberStart;

    private LocalDate memberEnd;

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
}
