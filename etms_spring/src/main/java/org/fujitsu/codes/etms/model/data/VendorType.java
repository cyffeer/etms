package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendor_type")
public class VendorType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_type_id")
    private Long vendorTypeId;

    @Column(name = "vendor_type_code", nullable = false, unique = true, length = 30)
    private String vendorTypeCode;

    @Column(name = "vendor_type_nm", nullable = false, length = 150)
    private String vendorTypeName;

    @Column(name = "is_active")
    private Boolean active = Boolean.TRUE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
