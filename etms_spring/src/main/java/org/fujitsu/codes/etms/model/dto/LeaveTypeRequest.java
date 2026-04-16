package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class LeaveTypeRequest {

    @NotBlank(message = "Leave type code is required")
    @Size(max = 30, message = "Leave type code must be at most 30 characters")
    private String leaveTypeCode;

    @NotBlank(message = "Leave type name is required")
    @Size(max = 120, message = "Leave type name must be at most 120 characters")
    private String leaveTypeName;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @NotNull(message = "Annual entitlement is required")
    @Min(value = 0, message = "Annual entitlement must be at least 0")
    @Max(value = 365, message = "Annual entitlement must be at most 365")
    private Integer annualEntitlementDays;

    private Boolean active = Boolean.TRUE;

    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }

    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAnnualEntitlementDays() {
        return annualEntitlementDays;
    }

    public void setAnnualEntitlementDays(Integer annualEntitlementDays) {
        this.annualEntitlementDays = annualEntitlementDays;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
