package org.fujitsu.codes.etms.model.dto;

public class LeaveBalanceResponse {

    private String employeeNumber;
    private String employeeName;
    private Integer year;
    private String leaveTypeCode;
    private String leaveTypeName;
    private Integer annualEntitlementDays;
    private Integer approvedDaysUsed;
    private Integer pendingDays;
    private Integer remainingDays;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }

    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public Integer getAnnualEntitlementDays() {
        return annualEntitlementDays;
    }

    public void setAnnualEntitlementDays(Integer annualEntitlementDays) {
        this.annualEntitlementDays = annualEntitlementDays;
    }

    public Integer getApprovedDaysUsed() {
        return approvedDaysUsed;
    }

    public void setApprovedDaysUsed(Integer approvedDaysUsed) {
        this.approvedDaysUsed = approvedDaysUsed;
    }

    public Integer getPendingDays() {
        return pendingDays;
    }

    public void setPendingDays(Integer pendingDays) {
        this.pendingDays = pendingDays;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }
}
