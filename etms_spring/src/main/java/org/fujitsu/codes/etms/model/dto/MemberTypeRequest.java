package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberTypeRequest {

    @NotBlank(message = "Member type code is required")
    @Size(max = 30, message = "Member type code must be at most 30 characters")
    private String memberTypeCode;

    @NotBlank(message = "Member type name is required")
    @Size(max = 150, message = "Member type name must be at most 150 characters")
    private String memberTypeName;

    private Boolean active = Boolean.TRUE;

    public String getMemberTypeCode() {
        return memberTypeCode;
    }

    public void setMemberTypeCode(String memberTypeCode) {
        this.memberTypeCode = memberTypeCode;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
