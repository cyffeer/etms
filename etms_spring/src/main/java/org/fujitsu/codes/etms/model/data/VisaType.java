package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "visa_type")
public class VisaType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visa_type_id")
    private Long visaTypeId;

    @Column(name = "visa_type_nm", nullable = false, unique = true, length = 30)
    private String visaTypeCode;

    @Transient
    private String visaTypeName;

    @Column(name = "description", length = 255)
    private String description;

    @Transient
    private Boolean active = Boolean.TRUE;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

    public Long getVisaTypeId() {
        return visaTypeId;
    }

    public void setVisaTypeId(Long visaTypeId) {
        this.visaTypeId = visaTypeId;
    }

    public String getVisaTypeCode() {
        return visaTypeCode;
    }

    public void setVisaTypeCode(String visaTypeCode) {
        this.visaTypeCode = visaTypeCode;
    }

    public String getVisaTypeName() {
        return visaTypeName != null ? visaTypeName : visaTypeCode;
    }

    public void setVisaTypeName(String visaTypeName) {
        this.visaTypeName = visaTypeName;
        if (this.visaTypeCode == null) {
            this.visaTypeCode = visaTypeName;
        }
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
