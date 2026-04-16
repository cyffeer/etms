package org.fujitsu.codes.etms.test.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.fujitsu.codes.etms.model.data.UserRole;
import org.fujitsu.codes.etms.security.EtmsAccessPolicy;
import org.junit.jupiter.api.Test;

class TestEtmsAccessPolicy {

    private final EtmsAccessPolicy accessPolicy = new EtmsAccessPolicy();

    @Test
    void employeesMeEndpointAllowsEmployeeSelfService() {
        Set<UserRole> getRoles = accessPolicy.resolveAllowedRoles("/api/employees/me", "GET");
        Set<UserRole> putRoles = accessPolicy.resolveAllowedRoles("/api/employees/me", "PUT");

        assertThat(getRoles).containsExactly(UserRole.EMPLOYEE);
        assertThat(putRoles).containsExactly(UserRole.EMPLOYEE);
    }

    @Test
    void employeeEventsWriteCanReachMethodAuthorization() {
        Set<UserRole> postRoles = accessPolicy.resolveAllowedRoles("/api/employee-events", "POST");

        assertThat(postRoles).contains(UserRole.ADMIN, UserRole.MANAGER, UserRole.EMPLOYEE);
    }

    @Test
    void skillsInventoryListIsNotExposedToEmployeeRole() {
        Set<UserRole> getRoles = accessPolicy.resolveAllowedRoles("/api/skills-inventory", "GET");
        Set<UserRole> employeeScopedGet = accessPolicy.resolveAllowedRoles("/api/skills-inventory/employee/EMP_001/skills", "GET");

        assertThat(getRoles).doesNotContain(UserRole.EMPLOYEE);
        assertThat(employeeScopedGet).contains(UserRole.EMPLOYEE);
    }

    @Test
    void dashboardAndNotificationsAreNotExposedToEmployeeRole() {
        Set<UserRole> dashboardRoles = accessPolicy.resolveAllowedRoles("/api/dashboard/summary", "GET");
        Set<UserRole> notificationRoles = accessPolicy.resolveAllowedRoles("/api/notifications", "GET");

        assertThat(dashboardRoles).containsExactlyInAnyOrder(UserRole.ADMIN, UserRole.HR, UserRole.MANAGER);
        assertThat(notificationRoles).containsExactlyInAnyOrder(UserRole.ADMIN, UserRole.HR, UserRole.MANAGER);
    }
}
