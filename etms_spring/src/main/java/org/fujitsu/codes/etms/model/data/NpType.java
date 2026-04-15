package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "np_type")
public class NpType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "np_type_id")
    private Long npTypeId;

    @Column(name = "np_type_code", nullable = false, unique = true, length = 30)
    private String npTypeCode;

    @Column(name = "np_type_nm", nullable = false, length = 150)
    private String npTypeName;

    @Column(name = "is_active")
    private Boolean active = Boolean.TRUE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
