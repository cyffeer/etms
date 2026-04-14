package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PassportInfoRequest {

    @NotBlank(message = "Passport number is required")
    @Size(max = 50, message = "Passport number must be at most 50 characters")
    private String passportNumber;

    @NotBlank(message = "Employee number is required")
    @Size(max = 30, message = "Employee number must be at most 30 characters")
    private String employeeNumber;

    private LocalDate issuedDate;
    private LocalDate expiryDate;

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
}