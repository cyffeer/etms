package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "vendor_type")
public class VendorType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_type_id")
    private Long vendorTypeId;

    @Column(name = "vendor_type_nm", nullable = false, unique = true, length = 30)
    private String vendorTypeCode;

    @Transient
    private String vendorTypeName;

    @Transient
    private Boolean active = Boolean.TRUE;

    @Transient
    private LocalDateTime createdAt;

    @Transient
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
        return vendorTypeName != null ? vendorTypeName : vendorTypeCode;
    }

    public void setVendorTypeName(String vendorTypeName) {
        this.vendorTypeName = vendorTypeName;
        if (this.vendorTypeCode == null) {
            this.vendorTypeCode = vendorTypeName;
        }
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
