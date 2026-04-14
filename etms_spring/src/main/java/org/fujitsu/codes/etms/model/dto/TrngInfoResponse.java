package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrngInfoResponse {

    private Long trngInfoId;
    private String trngCode;
    private String trngName;
    private String trngTypeCode;
    private String vendorCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getTrngInfoId() {
        return trngInfoId;
    }

    public void setTrngInfoId(Long trngInfoId) {
        this.trngInfoId = trngInfoId;
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