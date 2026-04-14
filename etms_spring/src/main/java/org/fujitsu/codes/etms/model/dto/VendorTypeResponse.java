package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;

public class VendorTypeResponse {

    private Long vendorTypeId;
    private String vendorTypeCode;
    private String vendorTypeName;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getVendorTypeId() {
        return vendorTypeId;
    }

    public void setVendorTypeId(Long vendorTypeId) {
        this.vendorTypeId = vendorTypeId;
    }

    public String getVendorTypeCode() {
        return vendorTypeCode;
    }

    public void setVendorTypeCode(String vendorTypeCode) {
        this.vendorTypeCode = vendorTypeCode;
    }

    public String getVendorTypeName() {
        return vendorTypeName;
    }

    public void setVendorTypeName(String vendorTypeName) {
        this.vendorTypeName = vendorTypeName;
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