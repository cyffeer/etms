package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NpTypeRequest {

    @NotBlank(message = "NP type code is required")
    @Size(max = 30, message = "NP type code must be at most 30 characters")
    private String npTypeCode;

    @NotBlank(message = "NP type name is required")
    @Size(max = 150, message = "NP type name must be at most 150 characters")
    private String npTypeName;

    private Boolean active = Boolean.TRUE;

    public String getNpTypeCode() {
        return npTypeCode;
    }

    public void setNpTypeCode(String npTypeCode) {
        this.npTypeCode = npTypeCode;
    }

    public String getNpTypeName() {
        return npTypeName;
    }

    public void setNpTypeName(String npTypeName) {
        this.npTypeName = npTypeName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}