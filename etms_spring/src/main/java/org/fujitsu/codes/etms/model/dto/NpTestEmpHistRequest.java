package org.fujitsu.codes.etms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NpTestEmpHistRequest {

    @NotBlank(message = "Employee number is required")
    @Size(max = 30, message = "Employee number must be at most 30 characters")
    private String employeeNumber;

    @NotNull(message = "Test id is required")
    private Long npTestHistId;

    private Boolean passFlag;
    private Boolean takeFlag;
    private Integer points;

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
}	