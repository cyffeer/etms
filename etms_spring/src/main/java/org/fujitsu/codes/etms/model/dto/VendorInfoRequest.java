package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VendorInfoRequest {

    @NotBlank(message = "Vendor code is required")
    @Size(max = 30, message = "Vendor code must be at most 30 characters")
    private String vendorCode;

    @NotBlank(message = "Vendor name is required")
    @Size(max = 150, message = "Vendor name must be at most 150 characters")
    private String vendorName;

    @NotBlank(message = "Vendor type code is required")
    @Size(max = 30, message = "Vendor type code must be at most 30 characters")
    private String vendorTypeCode;

    @Email(message = "Contact email format is invalid")
    @Size(max = 150, message = "Contact email must be at most 150 characters")
    private String contactEmail;

    @Size(max = 30, message = "Contact phone must be at most 30 characters")
    private String contactPhone;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String addressLine;

    private Boolean active = Boolean.TRUE;

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
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
}