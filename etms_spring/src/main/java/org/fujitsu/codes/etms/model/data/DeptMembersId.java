package org.fujitsu.codes.etms.model.data;

import java.io.Serializable;
import java.util.Objects;

public class DeptMembersId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String departmentCode;
    private Integer employeeNumber;

    public DeptMembersId() {
    }

    public DeptMembersId(String departmentCode, Integer employeeNumber) {
        this.departmentCode = departmentCode;
        this.employeeNumber = employeeNumber;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeptMembersId that)) return false;
        return Objects.equals(departmentCode, that.departmentCode)
                && Objects.equals(employeeNumber, that.employeeNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentCode, employeeNumber);
    }
}
