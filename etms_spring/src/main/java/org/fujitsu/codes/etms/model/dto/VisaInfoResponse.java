package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VisaInfoResponse {

    private Long visaInfoId;
    private String employeeNumber;
    private Long visaTypeId;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private Boolean cancelFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getVisaInfoId() {
        return visaInfoId;
    }

    public void setVisaInfoId(Long visaInfoId) {
        this.visaInfoId = visaInfoId;
    }

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
