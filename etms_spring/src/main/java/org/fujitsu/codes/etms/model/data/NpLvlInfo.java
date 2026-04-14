package org.fujitsu.codes.etms.model.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "np_lvl_info")
public class NpLvlInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "np_lvl_id")
    private Long npLvlInfoId;

    @Column(name = "np_lvl_info_code", nullable = false, unique = true, length = 30)
    private String npLvlInfoCode;

    @Column(name = "np_lvl_info_name", nullable = false, length = 150)
    private String npLvlInfoName;

    @Column(name = "np_type_code", nullable = false, length = 30)
    private String npTypeCode;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "allowance_amount", precision = 18, scale = 2)
    private BigDecimal allowanceAmount;

    @Column(name = "allowance_currency", length = 10)
    private String allowanceCurrency;

    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getNpLvlInfoId() {
        return npLvlInfoId;
    }

    public void setNpLvlInfoId(Long npLvlInfoId) {
        this.npLvlInfoId = npLvlInfoId;
    }

    public String getNpLvlInfoCode() {
        return npLvlInfoCode;
    }

    public void setNpLvlInfoCode(String npLvlInfoCode) {
        this.npLvlInfoCode = npLvlInfoCode;
    }

    public String getNpLvlInfoName() {
        return npLvlInfoName;
    }

    public void setNpLvlInfoName(String npLvlInfoName) {
        this.npLvlInfoName = npLvlInfoName;
    }

    public String getNpTypeCode() {
        return npTypeCode;
    }

    public void setNpTypeCode(String npTypeCode) {
        this.npTypeCode = npTypeCode;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public BigDecimal getAllowanceAmount() {
        return allowanceAmount;
    }

    public void setAllowanceAmount(BigDecimal allowanceAmount) {
        this.allowanceAmount = allowanceAmount;
    }

    public String getAllowanceCurrency() {
        return allowanceCurrency;
    }

    public void setAllowanceCurrency(String allowanceCurrency) {
        this.allowanceCurrency = allowanceCurrency;
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
