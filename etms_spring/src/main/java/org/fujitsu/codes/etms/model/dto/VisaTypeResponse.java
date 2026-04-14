package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;

public class VisaTypeResponse {

    private Long visaTypeId;
    private String visaTypeCode;
    private String visaTypeName;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getVisaTypeId() {
        return visaTypeId;
    }

    public void setVisaTypeId(Long visaTypeId) {
        this.visaTypeId = visaTypeId;
    }

    public String getVisaTypeCode() {
        return visaTypeCode;
    }

    public void setVisaTypeCode(String visaTypeCode) {
        this.visaTypeCode = visaTypeCode;
    }

    public String getVisaTypeName() {
        return visaTypeName;
    }

    public void setVisaTypeName(String visaTypeName) {
        this.visaTypeName = visaTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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