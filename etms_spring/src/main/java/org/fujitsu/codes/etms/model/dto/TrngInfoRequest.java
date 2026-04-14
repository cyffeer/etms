package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TrngInfoRequest {

    @NotBlank(message = "Training code is required")
    @Size(max = 30, message = "Training code must be at most 30 characters")
    private String trngCode;

    @NotBlank(message = "Training name is required")
    @Size(max = 150, message = "Training name must be at most 150 characters")
    private String trngName;

    @NotBlank(message = "Training type code is required")
    @Size(max = 30, message = "Training type code must be at most 30 characters")
    private String trngTypeCode;

    @Size(max = 30, message = "Vendor code must be at most 30 characters")
    private String vendorCode;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 150, message = "Location must be at most 150 characters")
    private String location;

    private Boolean active = Boolean.TRUE;

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
}