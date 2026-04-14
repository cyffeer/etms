package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "vendor_info")
public class VendorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "vendor_nm", nullable = false, unique = true, length = 150)
    private String vendorCode;

    @Transient
    private String vendorName;

    @Transient
    private String vendorTypeCode;

    @Transient
    private String contactEmail;

    @Transient
    private String contactPhone;

    @Transient
    private String addressLine;

    @Column(name = "active_flag", length = 1)
    @ColumnTransformer(
            read = "CASE WHEN active_flag = 'Y' THEN true WHEN active_flag = 'N' THEN false ELSE null END",
            write = "CASE WHEN ? THEN 'Y' ELSE 'N' END")
    private Boolean active = Boolean.TRUE;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
        if (this.vendorName == null) {
            this.vendorName = vendorCode;
        }
    }

    public String getVendorName() {
        return vendorName != null ? vendorName : vendorCode;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
        if (this.vendorCode == null) {
            this.vendorCode = vendorName;
        }
    }

    public String getVendorTypeCode() {
        return vendorTypeCode;
    }

    public void setVendorTypeCode(String vendorTypeCode) {
        this.vendorTypeCode = vendorTypeCode;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
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
