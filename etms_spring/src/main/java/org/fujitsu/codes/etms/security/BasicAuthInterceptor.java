package org.fujitsu.codes.etms.security;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.Login;
import org.fujitsu.codes.etms.model.data.UserRole;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class BasicAuthInterceptor implements HandlerInterceptor {

    public static final String AUTHENTICATED_USER_ATTR = "etms.authenticatedUser";

    private final AuthService authService;
    private final EtmsAccessPolicy accessPolicy;
    private final ObjectMapper objectMapper;

    public BasicAuthInterceptor(
            AuthService authService,
            EtmsAccessPolicy accessPolicy,
            ObjectMapper objectMapper) {
        this.authService = authService;
        this.accessPolicy = accessPolicy;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        if (requestUri == null || !requestUri.startsWith("/api/")) {
            return true;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || requestUri.startsWith("/api/login")) {
            return true;
        }

        try {
            Login authenticatedUser = authService.authenticateBasicHeader(request.getHeader("Authorization"));
            Set<UserRole> allowedRoles = accessPolicy.resolveAllowedRoles(requestUri, request.getMethod());
            UserRole role = authenticatedUser.getRole();
            if (role == null) {
                role = UserRole.EMPLOYEE;
                authenticatedUser.setRole(role);
            }

            if (!allowedRoles.contains(role)) {
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied",
                        "Role " + role.name() + " is not allowed to call this endpoint");
                return false;
            }

            request.setAttribute(AUTHENTICATED_USER_ATTR, authenticatedUser);
            return true;
        } catch (InvalidInputException ex) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed", ex.getMessage());
            return false;
        }
    }

    private void writeError(HttpServletResponse response, int status, String message, String detail) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(message, List.of(detail)));
    }
}
