package org.fujitsu.codes.etms.security;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.fujitsu.codes.etms.model.data.UserRole;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class EtmsAccessPolicy {

    private static final Set<UserRole> ALL_ROLES = EnumSet.allOf(UserRole.class);
    private static final Set<UserRole> MASTER_DATA_VIEW_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.MANAGER,
            UserRole.EMPLOYEE);
    private static final Set<UserRole> MASTER_DATA_WRITE_ROLES = EnumSet.of(UserRole.ADMIN);
    private static final Set<UserRole> EMPLOYEE_VIEW_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.MANAGER,
            UserRole.EMPLOYEE);
    private static final Set<UserRole> EMPLOYEE_WRITE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.MANAGER);
    private static final Set<UserRole> LEAVE_CREATE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.HR,
            UserRole.EMPLOYEE);
    private static final Set<UserRole> LEAVE_UPDATE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.HR,
            UserRole.MANAGER,
            UserRole.EMPLOYEE);
    private static final Set<UserRole> ATTENDANCE_CREATE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.HR,
            UserRole.EMPLOYEE);
    private static final Set<UserRole> ATTENDANCE_UPDATE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.HR,
            UserRole.MANAGER);
    private static final Set<UserRole> TRAINING_WRITE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.HR,
            UserRole.MANAGER);
    private static final Set<UserRole> TRAVEL_WRITE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.HR);
    private static final Set<UserRole> SKILLS_WRITE_ROLES = EnumSet.of(
            UserRole.ADMIN,
            UserRole.HR,
            UserRole.MANAGER);

    private static final List<String> MASTER_DATA_PREFIXES = List.of(
            "/api/departments",
            "/api/leave-types",
            "/api/member-types",
            "/api/visa-types",
            "/api/trng-types",
            "/api/vendors",
            "/api/vendor-types",
            "/api/np-types",
            "/api/np-lvl-info",
            "/api/skills",
            "/api/skill-levels");

    public Set<UserRole> resolveAllowedRoles(String requestUri, String method) {
        if (requestUri == null || requestUri.isBlank()) {
            return ALL_ROLES;
        }
        if (HttpMethod.OPTIONS.matches(method)) {
            return ALL_ROLES;
        }
        if (requestUri.startsWith("/api/login")) {
            return ALL_ROLES;
        }

        if (matchesAnyPrefix(requestUri, MASTER_DATA_PREFIXES)) {
            return isReadMethod(method) ? MASTER_DATA_VIEW_ROLES : MASTER_DATA_WRITE_ROLES;
        }

        if (requestUri.startsWith("/api/employees")) {
            return switch (normalizeMethod(method)) {
                case "GET", "HEAD" -> EMPLOYEE_VIEW_ROLES;
                case "POST" -> EMPLOYEE_WRITE_ROLES;
                case "PUT", "PATCH", "DELETE" -> EnumSet.of(UserRole.ADMIN);
                default -> ALL_ROLES;
            };
        }

        if (requestUri.startsWith("/api/dept-members")) {
            return switch (normalizeMethod(method)) {
                case "GET", "HEAD" -> EMPLOYEE_VIEW_ROLES;
                case "POST", "PUT", "PATCH" -> EnumSet.of(UserRole.ADMIN, UserRole.MANAGER);
                case "DELETE" -> EnumSet.of(UserRole.ADMIN);
                default -> ALL_ROLES;
            };
        }

        if (requestUri.startsWith("/api/leaves")) {
            return switch (normalizeMethod(method)) {
                case "GET", "HEAD" -> ALL_ROLES;
                case "POST" -> LEAVE_CREATE_ROLES;
                case "PUT", "PATCH" -> LEAVE_UPDATE_ROLES;
                case "DELETE" -> EnumSet.of(UserRole.ADMIN);
                default -> ALL_ROLES;
            };
        }

        if (requestUri.startsWith("/api/attendance")) {
            return switch (normalizeMethod(method)) {
                case "GET", "HEAD" -> ALL_ROLES;
                case "POST" -> ATTENDANCE_CREATE_ROLES;
                case "PUT", "PATCH" -> ATTENDANCE_UPDATE_ROLES;
                case "DELETE" -> EnumSet.of(UserRole.ADMIN);
                default -> ALL_ROLES;
            };
        }

        if (requestUri.startsWith("/api/skills-inventory")) {
            return isReadMethod(method) ? ALL_ROLES : SKILLS_WRITE_ROLES;
        }

        if (requestUri.startsWith("/api/passport-info") || requestUri.startsWith("/api/visa-info")) {
            return isReadMethod(method) ? ALL_ROLES : TRAVEL_WRITE_ROLES;
        }

        if (requestUri.startsWith("/api/trng-history")
                || requestUri.startsWith("/api/trng-info")
                || requestUri.startsWith("/api/np-test-hist")
                || requestUri.startsWith("/api/np-test-emp-hist")) {
            return isReadMethod(method) ? ALL_ROLES : TRAINING_WRITE_ROLES;
        }

        return ALL_ROLES;
    }

    private boolean matchesAnyPrefix(String requestUri, List<String> prefixes) {
        return prefixes.stream().anyMatch(requestUri::startsWith);
    }

    private boolean isReadMethod(String method) {
        String normalizedMethod = normalizeMethod(method);
        return "GET".equals(normalizedMethod) || "HEAD".equals(normalizedMethod);
    }

    private String normalizeMethod(String method) {
        return method == null ? "" : method.trim().toUpperCase();
    }
}
