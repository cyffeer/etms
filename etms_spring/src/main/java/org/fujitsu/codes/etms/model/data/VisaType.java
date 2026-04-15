package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visa_type")
public class VisaType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visa_type_id")
    private Long visaTypeId;

    @Column(name = "visa_type_code", nullable = false, unique = true, length = 30)
    private String visaTypeCode;

    @Column(name = "visa_type_nm", nullable = false, length = 150)
    private String visaTypeName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active")
    private Boolean active = Boolean.TRUE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
