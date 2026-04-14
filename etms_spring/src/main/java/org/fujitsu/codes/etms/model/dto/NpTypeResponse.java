package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;

public class NpTypeResponse {

    private Long npTypeId;
    private String npTypeCode;
    private String npTypeName;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getNpTypeId() {
        return npTypeId;
    }

    public void setNpTypeId(Long npTypeId) {
        this.npTypeId = npTypeId;
    }

    public String getNpTypeCode() {
        return npTypeCode;
    }

    public void setNpTypeCode(String npTypeCode) {
        this.npTypeCode = npTypeCode;
    }

    public String getNpTypeName() {
        return npTypeName;
    }

    public void setNpTypeName(String npTypeName) {
        this.npTypeName = npTypeName;
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