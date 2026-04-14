package org.fujitsu.codes.etms.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fujitsu.codes.etms.model.dto.LeaveRequest;

public final class LeaveValidator {

    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "APPROVED", "REJECTED", "CANCELLED");

    private LeaveValidator() {
    }

    public static List<String> validate(LeaveRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body is required");
            return errors;
        }

        if (isBlank(request.getEmployeeNumber())) {
            errors.add("Employee number is required");
        }

        if (isBlank(request.getLeaveType())) {
            errors.add("Leave type is required");
        }

        if (request.getStartDate() == null) {
            errors.add("Start date is required");
        }

        if (isBlank(request.getStatus())) {
            errors.add("Status is required");
        } else if (!ALLOWED_STATUS.contains(request.getStatus().trim().toUpperCase())) {
            errors.add("Status is invalid");
        }

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            errors.add("End date cannot be before start date");
        }

        return errors;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}