package org.fujitsu.codes.etms.validator;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class VisaInfoValidator {

    private VisaInfoValidator() {
    }

    public static List<String> validate(Object request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body is required");
            return errors;
        }

        String employeeNumber = readString(request, "getEmployeeNumber");
        if (isBlank(employeeNumber)) {
            errors.add("Employee number is required");
        }

        String visaType = firstNonBlank(
                readString(request, "getVisaType"),
                readString(request, "getVisaTypeCode"));
        if (isBlank(visaType)) {
            errors.add("Visa type is required");
        }

        LocalDate issueDate = readDate(request, "getIssueDate");
        LocalDate expiryDate = readDate(request, "getExpiryDate");
        if (issueDate != null && expiryDate != null && expiryDate.isBefore(issueDate)) {
            errors.add("Expiry date cannot be before issue date");
        }

        return errors;
    }

    private static String readString(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            Object val = m.invoke(target);
            return val == null ? null : String.valueOf(val);
        } catch (Exception ex) {
            return null;
        }
    }

    private static LocalDate readDate(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            Object val = m.invoke(target);
            return (val instanceof LocalDate) ? (LocalDate) val : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private static String firstNonBlank(String a, String b) {
        return !isBlank(a) ? a : b;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}