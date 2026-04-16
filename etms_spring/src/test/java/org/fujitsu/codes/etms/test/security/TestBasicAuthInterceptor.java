package org.fujitsu.codes.etms.test.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Writer;
import java.util.Set;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.data.UserRole;
import org.fujitsu.codes.etms.security.BasicAuthInterceptor;
import org.fujitsu.codes.etms.security.EtmsAccessPolicy;
import org.fujitsu.codes.etms.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import tools.jackson.databind.ObjectMapper;

class TestBasicAuthInterceptor {

    @Mock
    private AuthService authService;

    @Mock
    private EtmsAccessPolicy accessPolicy;

    @Mock
    private ObjectMapper objectMapper;

    private BasicAuthInterceptor interceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interceptor = new BasicAuthInterceptor(authService, accessPolicy, objectMapper);
    }

    @Test
    void preHandleShouldReturnUnauthorizedWhenCredentialsInvalid() throws Exception {
        when(authService.authenticateBasicHeader(any())).thenThrow(new InvalidInputException("Invalid username or password"));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/employees");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
        verify(objectMapper).writeValue(org.mockito.ArgumentMatchers.<Writer>any(), any());
    }

    @Test
    void preHandleShouldReturnForbiddenWhenRoleNotAllowed() throws Exception {
        Login login = new Login();
        login.setUsername("EMP_001");
        login.setRole(UserRole.EMPLOYEE);

        when(authService.authenticateBasicHeader(any())).thenReturn(login);
        when(accessPolicy.resolveAllowedRoles("/api/employees", "GET")).thenReturn(Set.of(UserRole.ADMIN));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/employees");
        request.addHeader("Authorization", "Basic Zm9vOmJhcg==");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
        verify(objectMapper).writeValue(org.mockito.ArgumentMatchers.<Writer>any(), any());
    }
}
