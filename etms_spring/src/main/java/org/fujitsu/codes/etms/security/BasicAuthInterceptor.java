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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.apache.logging.log4j.ThreadContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class BasicAuthInterceptor implements HandlerInterceptor {

    public static final String AUTHENTICATED_USER_ATTR = "etms.authenticatedUser";
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BasicAuthInterceptor.class);

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
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = java.util.UUID.randomUUID().toString();
        }
        ThreadContext.put("requestId", requestId);
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
                log.warn("Access denied for uri={} role={}", requestUri, role.name());
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied",
                        "Role " + role.name() + " is not allowed to call this endpoint");
                return false;
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticatedUser.getUsername(),
                    null,
                    java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute(AUTHENTICATED_USER_ATTR, authenticatedUser);
            log.info("Authenticated request uri={} role={}", requestUri, role.name());
            return true;
        } catch (InvalidInputException ex) {
            log.warn("Authentication failed for uri={}", requestUri);
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed", ex.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.clearContext();
        ThreadContext.remove("requestId");
    }

    private void writeError(HttpServletResponse response, int status, String message, String detail) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(message, List.of(detail)));
    }
}
