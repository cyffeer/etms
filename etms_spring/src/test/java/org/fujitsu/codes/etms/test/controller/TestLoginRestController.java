package org.fujitsu.codes.etms.test.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.fujitsu.codes.etms.controller.LoginRestController;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.data.UserRole;
import org.fujitsu.codes.etms.service.AuditTrailService;
import org.fujitsu.codes.etms.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TestLoginRestController {

    @Mock
    private AuthService authService;

    @Mock
    private AuditTrailService auditTrailService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LoginRestController controller = new LoginRestController(authService, auditTrailService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void loginShouldReturnOkWhenCredentialsValid() throws Exception {
        Login login = org.mockito.Mockito.mock(Login.class);
        when(login.getUsername()).thenReturn("admin");
        when(login.getRole()).thenReturn(UserRole.ADMIN);

        when(authService.authenticate(anyString(), anyString())).thenReturn(login);
        when(authService.issueToken(login)).thenReturn("token");
        when(authService.getTokenExpirationSeconds()).thenReturn(3600L);

        String body = """
                {
                  "username": "admin",
                  "password": "admin123"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void loginShouldReturnUnauthorizedWhenCredentialsInvalid() throws Exception {
        when(authService.authenticate(anyString(), anyString()))
                .thenThrow(new org.fujitsu.codes.etms.exception.InvalidInputException("Invalid username or password"));

        String body = """
                {
                  "username": "admin",
                  "password": "wrong"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
