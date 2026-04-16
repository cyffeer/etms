package org.fujitsu.codes.etms.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.dao.LeaveTypeDao;
import org.fujitsu.codes.etms.model.data.Employees;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.fujitsu.codes.etms.model.data.LeaveType;
import org.fujitsu.codes.etms.model.dto.LeaveBalanceResponse;
import org.springframework.stereotype.Service;

@Service
public class LeaveBalanceService {

    private final LeaveDao leaveDao;
    private final LeaveTypeDao leaveTypeDao;
    private final EmployeesDao employeesDao;

    public LeaveBalanceService(LeaveDao leaveDao, LeaveTypeDao leaveTypeDao, EmployeesDao employeesDao) {
        this.leaveDao = leaveDao;
        this.leaveTypeDao = leaveTypeDao;
        this.employeesDao = employeesDao;
    }

    public List<LeaveBalanceResponse> getBalances(String employeeNumber, Integer year) {
        int targetYear = year == null ? LocalDate.now().getYear() : year;
        List<String> employeeNumbers = resolveEmployeeNumbers(employeeNumber);
        List<LeaveType> leaveTypes = leaveTypeDao.findAll().stream()
                .sorted(Comparator.comparing(LeaveType::getLeaveTypeCode))
                .toList();

        List<LeaveBalanceResponse> rows = new ArrayList<>();
        for (String employeeNumberValue : employeeNumbers) {
            String employeeName = resolveEmployeeName(employeeNumberValue);
            List<LeaveRecord> leaves = leaveDao.findByEmployeeNumber(employeeNumberValue);

            for (LeaveType leaveType : leaveTypes) {
                int approvedDays = 0;
                int pendingDays = 0;

                for (LeaveRecord leave : leaves) {
                    if (leave.getLeaveType() == null || !leave.getLeaveType().equalsIgnoreCase(leaveType.getLeaveTypeCode())) {
                        continue;
                    }
                    int leaveDays = countDaysInYear(leave.getStartDate(), leave.getEndDate(), targetYear);
                    if (leaveDays <= 0) {
                        continue;
                    }

                    String status = leave.getStatus() == null ? "" : leave.getStatus().trim().toUpperCase();
                    if ("APPROVED".equals(status)) {
                        approvedDays += leaveDays;
                    } else if ("PENDING".equals(status)) {
                        pendingDays += leaveDays;
                    }
                }

                int entitlement = leaveType.getAnnualEntitlementDays() == null ? 0 : leaveType.getAnnualEntitlementDays();
                LeaveBalanceResponse response = new LeaveBalanceResponse();
                response.setEmployeeNumber(employeeNumberValue);
                response.setEmployeeName(employeeName);
                response.setYear(targetYear);
                response.setLeaveTypeCode(leaveType.getLeaveTypeCode());
                response.setLeaveTypeName(leaveType.getLeaveTypeName());
                response.setAnnualEntitlementDays(entitlement);
                response.setApprovedDaysUsed(approvedDays);
                response.setPendingDays(pendingDays);
                response.setRemainingDays(Math.max(entitlement - approvedDays, 0));
                rows.add(response);
            }
        }

        return rows;
    }

    private List<String> resolveEmployeeNumbers(String employeeNumber) {
        if (employeeNumber != null && !employeeNumber.isBlank()) {
            return List.of(employeeNumber.trim());
        }
        return leaveDao.findAll().stream()
                .map(LeaveRecord::getEmployeeNumber)
                .filter(code -> code != null && !code.isBlank())
                .distinct()
                .toList();
    }

    private String resolveEmployeeName(String employeeNumberValue) {
        return employeesDao.findAll().stream()
                .filter(employee -> matchesEmployee(employee, employeeNumberValue))
                .findFirst()
                .map(this::buildEmployeeName)
                .orElse(employeeNumberValue);
    }

    private boolean matchesEmployee(Employees employee, String employeeNumberValue) {
        if (employee == null || employeeNumberValue == null) {
            return false;
        }
        String trimmed = employeeNumberValue.trim();
        return (employee.getEmployeeCode() != null && employee.getEmployeeCode().equalsIgnoreCase(trimmed))
                || (employee.getEmployeeId() != null && employee.getEmployeeId().toString().equals(trimmed));
    }

    private String buildEmployeeName(Employees employee) {
        String firstName = employee.getFirstName() == null ? "" : employee.getFirstName().trim();
        String lastName = employee.getLastName() == null ? "" : employee.getLastName().trim();
        String combined = (firstName + " " + lastName).trim();
        return combined.isBlank() ? (employee.getEmployeeCode() == null ? "" : employee.getEmployeeCode()) : combined;
    }

    private int countDaysInYear(LocalDate startDate, LocalDate endDate, int year) {
        if (startDate == null) {
            return 0;
        }

        LocalDate windowStart = LocalDate.of(year, 1, 1);
        LocalDate windowEnd = LocalDate.of(year, 12, 31);
        LocalDate effectiveStart = startDate.isBefore(windowStart) ? windowStart : startDate;
        LocalDate effectiveEnd = endDate == null ? startDate : endDate;
        if (effectiveEnd.isAfter(windowEnd)) {
            effectiveEnd = windowEnd;
        }
        if (effectiveEnd.isBefore(effectiveStart)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(effectiveStart, effectiveEnd) + 1;
    }
}
