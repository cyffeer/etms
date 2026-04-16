package org.fujitsu.codes.etms.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;

import org.fujitsu.codes.etms.controller.TrainingHistoryRestController;
import org.fujitsu.codes.etms.controller.TrngInfoRestController;
import org.fujitsu.codes.etms.controller.TrngTypeRestController;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

class TestTrainingRbacAnnotations {

    @Test
    void trngInfoMutationEndpointsShouldBeHrOrAdminOnly() throws Exception {
        assertPreAuthorize(TrngInfoRestController.class, "create", "hasAnyRole('ADMIN','HR')", org.fujitsu.codes.etms.model.dto.TrngInfoRequest.class);
        assertPreAuthorize(TrngInfoRestController.class, "update", "hasAnyRole('ADMIN','HR')", Long.class, org.fujitsu.codes.etms.model.dto.TrngInfoRequest.class);
        assertPreAuthorize(TrngInfoRestController.class, "delete", "hasAnyRole('ADMIN','HR')", Long.class);
        assertPreAuthorize(TrngInfoRestController.class, "uploadCertificate", "hasAnyRole('ADMIN','HR')", Long.class, MultipartFile.class);
    }

    @Test
    void trainingHistoryMutationEndpointsShouldBeHrOrAdminOnly() throws Exception {
        assertPreAuthorize(TrainingHistoryRestController.class, "assignTrainingToEmployee", "hasAnyRole('ADMIN','HR')", org.fujitsu.codes.etms.model.dto.TrngHistRequest.class);
        assertPreAuthorize(TrainingHistoryRestController.class, "deleteTrainingHistory", "hasAnyRole('ADMIN','HR')", Long.class, String.class);
    }

    @Test
    void trainingTypeMutationEndpointsShouldBeAdminOnly() throws Exception {
        assertPreAuthorize(TrngTypeRestController.class, "create", "hasRole('ADMIN')", org.fujitsu.codes.etms.model.dto.TrngTypeRequest.class);
        assertPreAuthorize(TrngTypeRestController.class, "update", "hasRole('ADMIN')", Long.class, org.fujitsu.codes.etms.model.dto.TrngTypeRequest.class);
        assertPreAuthorize(TrngTypeRestController.class, "delete", "hasRole('ADMIN')", Long.class);
    }

    private void assertPreAuthorize(Class<?> type, String methodName, String expected, Class<?>... parameterTypes) throws Exception {
        Method method = type.getDeclaredMethod(methodName, parameterTypes);
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        assertNotNull(preAuthorize, () -> type.getSimpleName() + "#" + methodName + " should have @PreAuthorize");
        assertEquals(expected, preAuthorize.value());
    }
}
