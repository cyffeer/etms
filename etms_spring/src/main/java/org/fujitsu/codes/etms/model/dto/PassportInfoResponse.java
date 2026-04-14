package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PassportInfoResponse {

    private Long passportInfoId;
    private String passportNumber;
    private String employeeNumber;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getPassportInfoId() {
        return passportInfoId;
    }

    public void setPassportInfoId(Long passportInfoId) {
        this.passportInfoId = passportInfoId;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
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