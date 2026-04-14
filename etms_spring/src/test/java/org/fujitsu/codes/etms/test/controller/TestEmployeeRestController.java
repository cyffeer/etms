package org.fujitsu.codes.etms.test.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.fujitsu.codes.etms.controller.EmployeeRestController;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.Employees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TestEmployeeRestController {

    @Mock
    private EmployeesDao employeesDao;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EmployeeRestController controller = new EmployeeRestController(employeesDao, null);
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
}