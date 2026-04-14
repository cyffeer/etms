package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TrngHistRequest {

    @NotNull(message = "Training id is required")
    private Long trngId;

    @NotBlank(message = "Employee number is required")
    @Size(max = 30, message = "Employee number must be at most 30 characters")
    private String employeeNumber;

    public Long getTrngId() {
        return trngId;
    }

    public void setTrngId(Long trngId) {
        this.trngId = trngId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
