package org.fujitsu.codes.etms.service;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EtmsAuthorizationService {

    private static final java.util.Set<String> HR_RESTRICTED_EVENT_TYPES = java.util.Set.of(
            "PROJECT_ASSIGNMENT",
            "RESIGNATION",
            "SUSPENSION",
            "TERMINATION");

    private final EmployeesDao employeesDao;

    public EtmsAuthorizationService(EmployeesDao employeesDao) {
        this.employeesDao = employeesDao;
    }

    public boolean canManageEmployeeRecords(Authentication authentication) {
        return hasAnyRole(authentication, UserRole.ADMIN, UserRole.HR, UserRole.MANAGER);
    }

    public boolean canViewEmployee(Authentication authentication, Long employeeId) {
        if (employeeId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (hasAnyRole(authentication, UserRole.ADMIN, UserRole.HR, UserRole.MANAGER)) {
            return true;
        }
        return isSelf(authentication, employeeId);
    }

    public boolean canAccessEmployeeInput(Authentication authentication, String employeeNumber) {
        if (employeeNumber == null || employeeNumber.isBlank()) {
            return false;
        }
        if (hasAnyRole(authentication, UserRole.ADMIN, UserRole.HR, UserRole.MANAGER)) {
            return true;
        }
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(employeeNumber);
        return employeeId != null && isSelf(authentication, employeeId.longValue());
    }

    public boolean canManageDepartmentRecords(Authentication authentication) {
        return hasAnyRole(authentication, UserRole.ADMIN);
    }

    public boolean canViewEmployeeEvent(Authentication authentication, String eventType, String employeeNumber) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String normalizedEventType = normalize(eventType);
        if (hasAnyRole(authentication, UserRole.ADMIN, UserRole.MANAGER)) {
            return true;
        }
        if (hasAnyRole(authentication, UserRole.HR)) {
            return normalizedEventType != null && !HR_RESTRICTED_EVENT_TYPES.contains(normalizedEventType);
        }
        if (!hasAnyRole(authentication, UserRole.EMPLOYEE)) {
            return false;
        }
        if (normalizedEventType == null) {
            return false;
        }
        if (employeeNumber == null || employeeNumber.isBlank()) {
            return false;
        }
        return isSelfEmployeeNumber(authentication, employeeNumber);
    }

    public boolean canMaintainEmployeeEvent(Authentication authentication, String eventType, String employeeNumber) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String normalizedEventType = normalize(eventType);
        if (normalizedEventType == null) {
            return false;
        }

        if (hasAnyRole(authentication, UserRole.ADMIN)) {
            return true;
        }

        if (hasAnyRole(authentication, UserRole.MANAGER)) {
            return "PROJECT_ASSIGNMENT".equals(normalizedEventType);
        }

        if (!hasAnyRole(authentication, UserRole.EMPLOYEE)) {
            return false;
        }

        if (!isSelfEmployeeNumber(authentication, employeeNumber)) {
            return false;
        }

        return "RESIGNATION".equals(normalizedEventType);
    }

    public boolean canApproveEmployeeEvent(Authentication authentication, String eventType) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String normalizedEventType = normalize(eventType);
        if (normalizedEventType == null) {
            return false;
        }
        if (hasAnyRole(authentication, UserRole.ADMIN)) {
            return true;
        }
        if (hasAnyRole(authentication, UserRole.MANAGER)) {
            return "PROJECT_ASSIGNMENT".equals(normalizedEventType);
        }
        return false;
    }

    private boolean isSelf(Authentication authentication, Long employeeId) {
        Integer currentEmployeeId = employeesDao.resolveEmployeeIdentifier(authentication.getName());
        return currentEmployeeId != null && currentEmployeeId.longValue() == employeeId.longValue();
    }

    private boolean isSelfEmployeeNumber(Authentication authentication, String employeeNumber) {
        if (authentication == null || employeeNumber == null || employeeNumber.isBlank()) {
            return false;
        }
        Integer currentEmployeeId = employeesDao.resolveEmployeeIdentifier(authentication.getName());
        Integer candidateEmployeeId = employeesDao.resolveEmployeeIdentifier(employeeNumber);
        if (currentEmployeeId != null && candidateEmployeeId != null && currentEmployeeId.equals(candidateEmployeeId)) {
            return true;
        }

        String current = authentication.getName() == null ? "" : authentication.getName().trim().toLowerCase();
        String candidate = employeeNumber.trim().toLowerCase();
        if (current.equals(candidate)) {
            return true;
        }
        String currentDigits = current.replaceAll("\\D+", "");
        String candidateDigits = candidate.replaceAll("\\D+", "");
        return !currentDigits.isBlank() && currentDigits.equals(candidateDigits);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private boolean hasAnyRole(Authentication authentication, UserRole... roles) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return java.util.Arrays.stream(roles)
                .anyMatch(role -> authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.name())));
    }
}
