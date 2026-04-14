package org.fujitsu.codes.etms.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NpLvlInfoRequest {

    @NotBlank(message = "NP level info code is required")
    @Size(max = 30, message = "NP level info code must be at most 30 characters")
    private String npLvlInfoCode;

    @NotBlank(message = "NP level info name is required")
    @Size(max = 150, message = "NP level info name must be at most 150 characters")
    private String npLvlInfoName;

    @NotBlank(message = "NP type code is required")
    @Size(max = 30, message = "NP type code must be at most 30 characters")
    private String npTypeCode;

    private LocalDate validFrom;
    private LocalDate validTo;

    private BigDecimal allowanceAmount;

    @Size(max = 10, message = "Allowance currency must be at most 10 characters")
    private String allowanceCurrency;

    private Boolean active = Boolean.TRUE;

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
}