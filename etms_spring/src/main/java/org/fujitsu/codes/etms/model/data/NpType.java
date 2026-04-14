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
@Table(name = "np_type")
public class NpType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "np_type_id")
    private Long npTypeId;

    @Column(name = "np_type_nm", nullable = false, unique = true, length = 30)
    private String npTypeCode;

    @Transient
    private String npTypeName;

    @Transient
    private Boolean active = Boolean.TRUE;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

    public Long getNpTypeId() {
        return npTypeId;
    }

    public void setNpTypeId(Long npTypeId) {
        this.npTypeId = npTypeId;
    }

    public String getNpTypeCode() {
        return npTypeCode;
    }

    public void setNpTypeCode(String npTypeCode) {
        this.npTypeCode = npTypeCode;
    }

    public String getNpTypeName() {
        return npTypeName != null ? npTypeName : npTypeCode;
    }

    public void setNpTypeName(String npTypeName) {
        this.npTypeName = npTypeName;
        if (this.npTypeCode == null) {
            this.npTypeCode = npTypeName;
        }
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
