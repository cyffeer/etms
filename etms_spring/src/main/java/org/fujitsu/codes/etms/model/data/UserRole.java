package org.fujitsu.codes.etms.model.data;

public enum UserRole {
    ADMIN,
    HR,
    MANAGER,
    EMPLOYEE;

    public static UserRole fromValue(String value) {
        if (value == null || value.isBlank()) {
            return EMPLOYEE;
        }
        return UserRole.valueOf(value.trim().toUpperCase());
    }
}
