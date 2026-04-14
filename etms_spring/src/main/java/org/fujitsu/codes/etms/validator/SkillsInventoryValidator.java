package org.fujitsu.codes.etms.validator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class SkillsInventoryValidator {

    private SkillsInventoryValidator() {
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

        String skillCode = readString(request, "getSkillCode");
        String skillName = readString(request, "getSkillName");
        if (isBlank(skillCode) && isBlank(skillName)) {
            errors.add("Skill code or skill name is required");
        }

        Integer years = readInteger(request, "getYearsOfExperience");
        if (years != null && years < 0) {
            errors.add("Years of experience cannot be negative");
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

    private static Integer readInteger(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            Object val = m.invoke(target);
            return (val instanceof Integer) ? (Integer) val : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}