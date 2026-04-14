package org.fujitsu.codes.etms.test.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.fujitsu.codes.etms.model.dto.LeaveRequest;
import org.fujitsu.codes.etms.validator.LeaveValidator;
import org.junit.jupiter.api.Test;

class TestLeaveValidator {

    @Test
    void validateShouldReturnErrorsWhenRequiredFieldsAreMissing() {
        LeaveRequest request = new LeaveRequest();

        List<String> errors = LeaveValidator.validate(request);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateShouldPassWhenRequestIsValid() {
        LeaveRequest request = validRequest();

        List<String> errors = LeaveValidator.validate(request);

        assertTrue(errors.isEmpty());
    }

    private LeaveRequest validRequest() {
        LeaveRequest request = new LeaveRequest();
        request.setEmployeeNumber("E1001");
        request.setLeaveType("Annual");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(5));
        request.setStatus("Pending");
        request.setRemarks("Planned leave");
        return request;
    }
}