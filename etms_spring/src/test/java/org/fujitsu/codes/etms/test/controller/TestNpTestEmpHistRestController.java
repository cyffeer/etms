package org.fujitsu.codes.etms.test.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.fujitsu.codes.etms.controller.NpTestEmpHistRestController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

class TestNpTestEmpHistRestController {

    @Test
    void shouldHaveBaseMappingAndGetEndpoint() {
        NpTestEmpHistRestController controller = instantiate(NpTestEmpHistRestController.class);
        assertNotNull(controller);
        assertNotNull(NpTestEmpHistRestController.class.getAnnotation(RequestMapping.class));

        boolean hasGet = Arrays.stream(NpTestEmpHistRestController.class.getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(GetMapping.class));
        assertTrue(hasGet);
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructors()[0];
            ctor.setAccessible(true);
            Object[] args = Arrays.stream(ctor.getParameterTypes())
                    .map(t -> t.isPrimitive() ? defaultValue(t) : Mockito.mock(t))
                    .toArray();
            return type.cast(ctor.newInstance(args));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object defaultValue(Class<?> t) {
        if (t == boolean.class) return false;
        if (t == char.class) return '\0';
        return 0;
    }
}