package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class NpTestEmpHistResponse {

    private Long npTestEmpHistId;
    private String employeeNumber;
    private Long npTestHistId;
    private String npTypeCode;
    private String npTypeName;
    private String npLvlInfoCode;
    private String npLvlInfoName;
    private Integer policyRank;
    private LocalDate allowanceStartDate;
    private LocalDate allowanceEndDate;
    private LocalDate effectiveAllowanceEndDate;
    private Boolean expired;
    private Boolean firstTimePass;
    private Boolean passFlag;
    private Boolean takeFlag;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getNpTestEmpHistId() {
        return npTestEmpHistId;
    }

    public void setNpTestEmpHistId(Long npTestEmpHistId) {
        this.npTestEmpHistId = npTestEmpHistId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getNpTestHistId() {
        return npTestHistId;
    }

    public void setNpTestHistId(Long npTestHistId) {
        this.npTestHistId = npTestHistId;
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

    public Integer getPolicyRank() {
        return policyRank;
    }

    public void setPolicyRank(Integer policyRank) {
        this.policyRank = policyRank;
    }

    public LocalDate getAllowanceStartDate() {
        return allowanceStartDate;
    }

    public void setAllowanceStartDate(LocalDate allowanceStartDate) {
        this.allowanceStartDate = allowanceStartDate;
    }

    public LocalDate getAllowanceEndDate() {
        return allowanceEndDate;
    }

    public void setAllowanceEndDate(LocalDate allowanceEndDate) {
        this.allowanceEndDate = allowanceEndDate;
    }

    public LocalDate getEffectiveAllowanceEndDate() {
        return effectiveAllowanceEndDate;
    }

    public void setEffectiveAllowanceEndDate(LocalDate effectiveAllowanceEndDate) {
        this.effectiveAllowanceEndDate = effectiveAllowanceEndDate;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Boolean getFirstTimePass() {
        return firstTimePass;
    }

    public void setFirstTimePass(Boolean firstTimePass) {
        this.firstTimePass = firstTimePass;
    }

    public Boolean getPassFlag() {
        return passFlag;
    }

    public void setPassFlag(Boolean passFlag) {
        this.passFlag = passFlag;
    }

    public Boolean getTakeFlag() {
        return takeFlag;
    }

    public void setTakeFlag(Boolean takeFlag) {
        this.takeFlag = takeFlag;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
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
