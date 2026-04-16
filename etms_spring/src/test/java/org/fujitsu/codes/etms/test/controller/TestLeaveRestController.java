package org.fujitsu.codes.etms.test.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.controller.LeaveRestController;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.dao.LeaveTypeDao;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.fujitsu.codes.etms.model.dto.LeaveRequest;
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

@ExtendWith(MockitoExtension.class)
class TestLeaveRestController {

    @Mock
    private LeaveDao leaveDao;

    @Mock
    private EmployeesDao employeesDao;

    @Mock
    private LeaveTypeDao leaveTypeDao;

    private LeaveRestController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller = new LeaveRestController(leaveDao, employeesDao, leaveTypeDao);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void searchShouldReturnForbiddenWhenEmployeeRequestsOtherEmployee() {
        var auth = new UsernamePasswordAuthenticationToken(
                "EMP_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        ResponseEntity<ApiResponse<?>> response = controller.search(
                auth,
                "EMP_999",
                "Vacation",
                "APPROVED",
                null,
                null,
                null,
                null);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
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
                .andExpect(status().isOk());
    }

    @Test
    void getByEmployeeShouldReturnForbiddenForOtherEmployee() {
        var auth = new UsernamePasswordAuthenticationToken(
                "EMP_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        ResponseEntity<ApiResponse<?>> response = controller.getByEmployee(auth, "EMP_999");

        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

    @Test
    void updateShouldReturnForbiddenWhenEmployeeUpdatesNonPendingLeave() {
        var auth = new UsernamePasswordAuthenticationToken(
                "EMP_001",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        LeaveRecord existing = new LeaveRecord();
        existing.setLeaveRecordId(100L);
        existing.setEmployeeNumber("EMP_001");
        existing.setStatus("APPROVED");
        when(leaveDao.findById(100L)).thenReturn(Optional.of(existing));

        LeaveRequest request = new LeaveRequest();
        request.setEmployeeNumber("EMP_001");
        request.setLeaveType("Vacation");
        request.setStartDate(java.time.LocalDate.now());
        request.setEndDate(java.time.LocalDate.now());
        request.setStatus("APPROVED");

        ResponseEntity<ApiResponse<?>> response = controller.update(auth, 100L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }
}
