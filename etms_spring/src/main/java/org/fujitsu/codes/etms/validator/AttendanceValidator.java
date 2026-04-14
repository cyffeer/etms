package org.fujitsu.codes.etms.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fujitsu.codes.etms.model.dto.AttendanceRequest;

public final class AttendanceValidator {

    private static final Set<String> ALLOWED_STATUS = Set.of("PRESENT", "ABSENT", "LATE", "HALF_DAY", "WFH");

    private AttendanceValidator() {
    }

    public static List<String> validate(AttendanceRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body is required");
            return errors;
        }

        if (isBlank(request.getEmployeeNumber())) {
            errors.add("Employee number is required");
        }

        if (request.getAttendanceDate() == null) {
            errors.add("Attendance date is required");
        }

        if (request.getTimeIn() != null && request.getTimeOut() != null
                && request.getTimeOut().isBefore(request.getTimeIn())) {
            errors.add("Time out cannot be before time in");
        }

        if (!isBlank(request.getStatus()) && !ALLOWED_STATUS.contains(request.getStatus().trim().toUpperCase())) {
            errors.add("Status is invalid");
        }

        return errors;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}