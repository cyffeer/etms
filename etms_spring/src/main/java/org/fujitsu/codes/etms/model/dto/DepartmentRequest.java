package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartmentRequest {

    @NotBlank(message = "Department code is required")
    @Size(max = 30, message = "Department code must be at most 30 characters")
    private String departmentCode;

    @NotBlank(message = "Department name is required")
    @Size(max = 150, message = "Department name must be at most 150 characters")
    private String departmentName;

    private Boolean active = Boolean.TRUE;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}