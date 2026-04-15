package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EmployeeEventRequest {

    @NotBlank(message = "Employee number is required")
    @Size(max = 30, message = "Employee number must be at most 30 characters")
    private String employeeNumber;

    @NotBlank(message = "Event type is required")
    @Size(max = 40, message = "Event type must be at most 40 characters")
    private String eventType;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Size(max = 30, message = "Department code must be at most 30 characters")
    private String departmentCode;

    @Size(max = 50, message = "Reference code must be at most 50 characters")
    private String referenceCode;

    @NotNull(message = "Effective date is required")
    private LocalDate effectiveDate;

    private LocalDate endDate;

    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status must be at most 20 characters")
    private String status;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
