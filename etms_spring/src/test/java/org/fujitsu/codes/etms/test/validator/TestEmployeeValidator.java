package org.fujitsu.codes.etms.test.validator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;

import org.fujitsu.codes.etms.model.dto.EmployeeRequest;
import org.fujitsu.codes.etms.validator.EmployeeValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TestEmployeeValidator {

    private final EmployeeValidator validator = instantiate(EmployeeValidator.class);

    @Test
    void validateCreateShouldReturnNoErrorsForBlankRequest() {
        EmployeeRequest request = new EmployeeRequest();

        List<String> errors = validator.validateCreate(request);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreateShouldPassWhenRequestIsValid() {
        EmployeeRequest request = validRequest();

        List<String> errors = validator.validateCreate(request);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdateShouldPassWhenRequestIsValid() {
        EmployeeRequest request = validRequest();

        List<String> errors = validator.validateUpdate(1L, request);

        assertTrue(errors.isEmpty());
    }

    private EmployeeRequest validRequest() {
        EmployeeRequest request = new EmployeeRequest();
        request.setEmployeeCode("E1001");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setHireDate(LocalDate.now().minusYears(1));
        request.setActive(Boolean.TRUE);
        return request;
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructors()[0];
            ctor.setAccessible(true);
            Object[] args = new Object[ctor.getParameterCount()];
            Class<?>[] paramTypes = ctor.getParameterTypes();

            for (int i = 0; i < paramTypes.length; i++) {
                args[i] = paramTypes[i].isPrimitive() ? defaultValue(paramTypes[i]) : Mockito.mock(paramTypes[i]);
            }

            return type.cast(ctor.newInstance(args));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        if (type == char.class) return '\0';
        return null;
    }
}