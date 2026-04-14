package org.fujitsu.codes.etms.validator;

import java.util.ArrayList;
import java.util.List;

import org.fujitsu.codes.etms.model.dto.LoginRequest;

public final class LoginValidator {

    private LoginValidator() {
    }

    public static List<String> validate(LoginRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body is required");
            return errors;
        }

        if (isBlank(request.getUsername())) {
            errors.add("Username is required");
        }

        if (isBlank(request.getPassword())) {
            errors.add("Password is required");
        }

        return errors;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}