package org.fujitsu.codes.etms.test.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.Employees;
import org.fujitsu.codes.etms.service.EtmsAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class TestEtmsAuthorizationService {

    @Mock
    private EmployeesDao employeesDao;

    private EtmsAuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new EtmsAuthorizationService(employeesDao);
    }

    @Test
    void employeeCanOnlyViewOwnEmployeeRecord() {
        when(employeesDao.resolveEmployeeIdentifier("EMP_001")).thenReturn(1);
        when(employeesDao.resolveEmployeeIdentifier("EMP_002")).thenReturn(2);

        Authentication self = new UsernamePasswordAuthenticationToken(
                "EMP_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        assertThat(authorizationService.canViewEmployee(self, 1L)).isTrue();
        assertThat(authorizationService.canViewEmployee(self, 2L)).isFalse();
        assertThat(authorizationService.canAccessEmployeeInput(self, "EMP_001")).isTrue();
        assertThat(authorizationService.canAccessEmployeeInput(self, "EMP_002")).isFalse();
    }

    @Test
    void hrCannotViewRestrictedEmployeeEvents() {
        Authentication hr = new UsernamePasswordAuthenticationToken(
                "HR_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_HR")));

        assertThat(authorizationService.canViewEmployeeEvent(hr, "PROMOTION", "EMP_001")).isTrue();
        assertThat(authorizationService.canViewEmployeeEvent(hr, "PROJECT_ASSIGNMENT", "EMP_001")).isFalse();
        assertThat(authorizationService.canViewEmployeeEvent(hr, "RESIGNATION", "EMP_001")).isFalse();
        assertThat(authorizationService.canViewEmployeeEvent(hr, "SUSPENSION", "EMP_001")).isFalse();
        assertThat(authorizationService.canViewEmployeeEvent(hr, "TERMINATION", "EMP_001")).isFalse();
    }

    @Test
    void employeeCanMaintainOnlyOwnResignation() {
        Authentication employee = new UsernamePasswordAuthenticationToken(
                "EMP_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        assertThat(authorizationService.canMaintainEmployeeEvent(employee, "RESIGNATION", "EMP_001")).isTrue();
        assertThat(authorizationService.canMaintainEmployeeEvent(employee, "RESIGNATION", "EMP_002")).isFalse();
        assertThat(authorizationService.canMaintainEmployeeEvent(employee, "PROJECT_ASSIGNMENT", "EMP_001")).isFalse();
    }

    @Test
    void employeeCanViewOwnSuspensionAndTerminationOnly() {
        Authentication employee = new UsernamePasswordAuthenticationToken(
                "EMP_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        assertThat(authorizationService.canViewEmployeeEvent(employee, "SUSPENSION", "EMP_001")).isTrue();
        assertThat(authorizationService.canViewEmployeeEvent(employee, "TERMINATION", "EMP_001")).isTrue();
        assertThat(authorizationService.canViewEmployeeEvent(employee, "SUSPENSION", "EMP_002")).isFalse();
        assertThat(authorizationService.canViewEmployeeEvent(employee, "TERMINATION", "EMP_002")).isFalse();
    }
}
