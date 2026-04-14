package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VisaTypeRequest {

    @NotBlank(message = "Visa type code is required")
    @Size(max = 30, message = "Visa type code must be at most 30 characters")
    private String visaTypeCode;

    @NotBlank(message = "Visa type name is required")
    @Size(max = 150, message = "Visa type name must be at most 150 characters")
    private String visaTypeName;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    private Boolean active = Boolean.TRUE;

    public String getVisaTypeCode() {
        return visaTypeCode;
    }

    public void setVisaTypeCode(String visaTypeCode) {
        this.visaTypeCode = visaTypeCode;
    }

    public String getVisaTypeName() {
        return visaTypeName;
    }

    public void setVisaTypeName(String visaTypeName) {
        this.visaTypeName = visaTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
