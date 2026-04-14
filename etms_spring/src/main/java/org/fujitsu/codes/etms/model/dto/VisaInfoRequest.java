package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VisaInfoRequest {

    @NotBlank(message = "Employee number is required")
    @Size(max = 30, message = "Employee number must be at most 30 characters")
    private String employeeNumber;

    @NotNull(message = "Visa type id is required")
    private Long visaTypeId;

    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private Boolean cancelFlag = Boolean.FALSE;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getVisaTypeId() {
        return visaTypeId;
    }

    public void setVisaTypeId(Long visaTypeId) {
        this.visaTypeId = visaTypeId;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(Boolean cancelFlag) {
        this.cancelFlag = cancelFlag;
    }
}