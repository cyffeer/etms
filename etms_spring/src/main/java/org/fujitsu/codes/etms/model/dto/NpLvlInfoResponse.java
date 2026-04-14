package org.fujitsu.codes.etms.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class NpLvlInfoResponse {

    private Long npLvlInfoId;
    private String npLvlInfoCode;
    private String npLvlInfoName;
    private String npTypeCode;
    private LocalDate validFrom;
    private LocalDate validTo;
    private BigDecimal allowanceAmount;
    private String allowanceCurrency;
    private Boolean active;
    private LocalDateTime createdAt;
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