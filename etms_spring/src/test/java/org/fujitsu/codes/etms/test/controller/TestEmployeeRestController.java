package org.fujitsu.codes.etms.test.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.controller.EmployeeRestController;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.Employees;
import org.fujitsu.codes.etms.model.dto.EmployeeSelfUpdateRequest;
import org.fujitsu.codes.etms.validator.EmployeeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.ResponseEntity;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.EmployeeResponse;

@ExtendWith(MockitoExtension.class)
class TestEmployeeRestController {

    @Mock
    private EmployeesDao employeesDao;

    @Mock
    private EmployeeValidator employeeValidator;

    private EmployeeRestController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller = new EmployeeRestController(employeesDao, employeeValidator);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllShouldReturnOkWithValidPaging() throws Exception {
        Employees employee = org.mockito.Mockito.mock(Employees.class);

        when(employeesDao.findAll(eq(0), eq(10))).thenReturn(List.of(employee));
        when(employeesDao.countAll()).thenReturn(1L);

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(employeesDao).findAll(0, 10);
        verify(employeesDao).countAll();
    }

    @Test
    void getAllShouldReturnBadRequestWhenPagingInvalid() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMyProfileShouldUpdateOwnRecordOnlyAllowedFields() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "EMP_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        Employees existing = new Employees();
        existing.setEmployeeId(1L);
        existing.setEmployeeCode("EMP_001");
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@corp.com");
        existing.setActive(Boolean.TRUE);

        Employees updated = new Employees();
        updated.setEmployeeId(1L);
        updated.setEmployeeCode("EMP_001");
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setEmail("new@corp.com");
        updated.setActive(Boolean.TRUE);

        EmployeeSelfUpdateRequest request = new EmployeeSelfUpdateRequest();
        request.setFirstName("New");
        request.setEmail("new@corp.com");

        when(employeesDao.resolveEmployeeIdentifier("EMP_001")).thenReturn(1);
        when(employeesDao.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeValidator.validateUpdate(eq(1L), any())).thenReturn(List.of());
        when(employeesDao.update(eq(1L), any())).thenReturn(Optional.of(updated));

        ResponseEntity<ApiResponse<EmployeeResponse>> response = controller.updateMyProfile(authentication, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getEmployeeCode()).isEqualTo("EMP_001");
        assertThat(response.getBody().getData().getFirstName()).isEqualTo("New");
        assertThat(response.getBody().getData().getEmail()).isEqualTo("new@corp.com");
    }
}
