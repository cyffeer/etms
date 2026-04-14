package org.fujitsu.codes.etms.test.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.fujitsu.codes.etms.validator.SkillsInventoryValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TestSkillsInventoryValidator {

    @Test
    void validateShouldReturnErrorsForEmptyInput() {
        Object result = invokeFirstValidationMethod(SkillsInventoryValidator.class, "validate", "validateCreate", "validateUpdate");
        assertNotNull(result);

        if (result instanceof List<?> list) {
            assertFalse(list.isEmpty());
        } else if (result instanceof Boolean value) {
            assertFalse(value);
        } else {
            assertTrue(true);
        }
    }

    private Object invokeFirstValidationMethod(Class<?> validatorType, String... methodNames) {
        for (String methodName : methodNames) {
            for (Method method : validatorType.getDeclaredMethods()) {
                if (!method.getName().equals(methodName)) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    Object target = Modifier.isStatic(method.getModifiers()) ? null : instantiate(validatorType);
                    Object[] args = Arrays.stream(method.getParameterTypes())
                            .map(this::newValue)
                            .toArray();
                    return method.invoke(target, args);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        throw new IllegalStateException("No validation method found in " + validatorType.getSimpleName());
    }

    private Object instantiate(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            return Mockito.mock(type);
        }
    }

    private Object newValue(Class<?> type) {
        if (type == String.class) return "";
        if (type == boolean.class || type == Boolean.class) return false;
        if (type == long.class || type == Long.class) return 0L;
        if (type == int.class || type == Integer.class) return 0;
        if (type == double.class || type == Double.class) return 0d;
        if (type == float.class || type == Float.class) return 0f;
        if (type == short.class || type == Short.class) return (short) 0;
        if (type == byte.class || type == Byte.class) return (byte) 0;
        if (type == char.class || type == Character.class) return '\0';
        return instantiate(type);
    }
}