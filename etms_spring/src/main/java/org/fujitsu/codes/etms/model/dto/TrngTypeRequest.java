package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TrngTypeRequest {

    @NotBlank(message = "Training type name is required")
    @Size(max = 25, message = "Training type name must be at most 25 characters")
    private String trngTypeNm;

    @Size(max = 150, message = "Description must be at most 150 characters")
    private String description;

    public String getTrngTypeNm() {
        return trngTypeNm;
    }

    public void setTrngTypeNm(String trngTypeNm) {
        this.trngTypeNm = trngTypeNm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
