package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;

public class TrngHistResponse {

    private Long trngId;
    private String employeeNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
