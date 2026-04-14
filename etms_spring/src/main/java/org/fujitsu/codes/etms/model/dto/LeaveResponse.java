package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;

public class LeaveResponse {

    private Long leaveRecordId;
    private String employeeNumber;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String remarks;

    public Long getLeaveRecordId() {
        return leaveRecordId;
    }

    public void setLeaveRecordId(Long leaveRecordId) {
        this.leaveRecordId = leaveRecordId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}