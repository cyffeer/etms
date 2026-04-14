package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;

public class MemberTypeResponse {

    private Long memberTypeId;
    private String memberTypeCode;
    private String memberTypeName;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

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