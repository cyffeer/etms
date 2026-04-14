package org.fujitsu.codes.etms.validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dto.EmployeeRequest;
import org.springframework.stereotype.Component;

@Component
public class EmployeeValidator {

    private final EmployeesDao employeesDao;

    public EmployeeValidator(EmployeesDao employeesDao) {
        this.employeesDao = employeesDao;
    }

    public List<String> validateCreate(EmployeeRequest request) {
        List<String> errors = commonChecks(request);

        if (request.getEmployeeCode() != null && employeesDao.existsByEmployeeCode(request.getEmployeeCode().trim())) {
            errors.add("Employee code already exists");
        }

        if (request.getEmail() != null && employeesDao.existsByEmail(request.getEmail().trim())) {
            errors.add("Email already exists");
        }

        return errors;
    }

    public List<String> validateUpdate(Long employeeId, EmployeeRequest request) {
        List<String> errors = commonChecks(request);

        if (request.getEmployeeCode() != null
                && employeesDao.existsByEmployeeCodeExceptId(request.getEmployeeCode().trim(), employeeId)) {
            errors.add("Employee code already exists");
        }

        if (request.getEmail() != null
                && employeesDao.existsByEmailExceptId(request.getEmail().trim(), employeeId)) {
            errors.add("Email already exists");
        }

        return errors;
    }

    private List<String> commonChecks(EmployeeRequest request) {
        List<String> errors = new ArrayList<>();

        LocalDate hireDate = request.getHireDate();
        if (hireDate != null && hireDate.isAfter(LocalDate.now())) {
            errors.add("Hire date cannot be in the future");
        }

        return errors;
    }
}