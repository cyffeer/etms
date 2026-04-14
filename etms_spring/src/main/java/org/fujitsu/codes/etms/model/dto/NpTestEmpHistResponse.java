package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDateTime;

public class NpTestEmpHistResponse {

    private Long npTestEmpHistId;
    private String employeeNumber;
    private Long npTestHistId;
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