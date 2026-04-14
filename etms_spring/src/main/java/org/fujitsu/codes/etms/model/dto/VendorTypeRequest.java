package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VendorTypeRequest {

    @NotBlank(message = "Vendor type code is required")
    @Size(max = 30, message = "Vendor type code must be at most 30 characters")
    private String vendorTypeCode;

    @NotBlank(message = "Vendor type name is required")
    @Size(max = 150, message = "Vendor type name must be at most 150 characters")
    private String vendorTypeName;

    private Boolean active = Boolean.TRUE;

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
}