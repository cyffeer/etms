package org.fujitsu.codes.etms.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "trng_info")
public class TrngInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trng_id")
    private Long trngInfoId;

    @Column(name = "trng_type_id")
    private Long trngTypeId;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "description", length = 150)
    private String trngName;

    @Transient
    private String trngCode;

    @Transient
    private String trngTypeCode;

    @Transient
    private String vendorCode;

    @Transient
    private LocalDate startDate;

    @Transient
    private LocalDate endDate;

    @Transient
    private String location;

    @Transient
    private Boolean active = Boolean.TRUE;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

    public Long getTrngInfoId() {
        return trngInfoId;
    }

    public void setTrngInfoId(Long trngInfoId) {
        this.trngInfoId = trngInfoId;
    }

    public Long getTrngTypeId() {
        return trngTypeId;
    }

    public void setTrngTypeId(Long trngTypeId) {
        this.trngTypeId = trngTypeId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getTrngCode() {
        return trngCode;
    }

    public void setTrngCode(String trngCode) {
        this.trngCode = trngCode;
    }

    public String getTrngName() {
        return trngName;
    }

    public void setTrngName(String trngName) {
        this.trngName = trngName;
    }

    public String getTrngTypeCode() {
        return trngTypeCode;
    }

    public void setTrngTypeCode(String trngTypeCode) {
        this.trngTypeCode = trngTypeCode;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
