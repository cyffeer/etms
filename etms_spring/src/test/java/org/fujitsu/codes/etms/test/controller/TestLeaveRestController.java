package org.fujitsu.codes.etms.test.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.controller.LeaveRestController;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TestLeaveRestController {

    @Mock
    private LeaveDao leaveDao;

    @Mock
    private EmployeesDao employeesDao;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LeaveRestController controller = new LeaveRestController(leaveDao, employeesDao);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void searchShouldReturnOk() throws Exception {
        LeaveRecord record = org.mockito.Mockito.mock(LeaveRecord.class);
        when(leaveDao.search(eq("E1001"), eq("Vacation"), eq("APPROVED"))).thenReturn(List.of(record));

        mockMvc.perform(get("/api/leaves/search")
                        .param("employeeNumber", "E1001")
                        .param("leaveType", "Vacation")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk());

        verify(leaveDao).search("E1001", "Vacation", "APPROVED");
    }

    @Test
    void getByIdShouldReturnNotFoundWhenMissing() throws Exception {
        when(leaveDao.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/leaves/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenDeleted() throws Exception {
        when(leaveDao.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/leaves/1"))
                .andExpect(status().isNoContent());
    }
}