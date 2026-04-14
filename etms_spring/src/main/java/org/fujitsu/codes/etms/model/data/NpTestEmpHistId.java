package org.fujitsu.codes.etms.model.data;

import java.io.Serializable;
import java.util.Objects;

public class NpTestEmpHistId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long npTestHistId;
    private String employeeNumber;

    public NpTestEmpHistId() {
    }

    public NpTestEmpHistId(Long npTestHistId, String employeeNumber) {
        this.npTestHistId = npTestHistId;
        this.employeeNumber = employeeNumber;
    }

    public Long getNpTestHistId() {
        return npTestHistId;
    }

    public void setNpTestHistId(Long npTestHistId) {
        this.npTestHistId = npTestHistId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NpTestEmpHistId that)) return false;
        return Objects.equals(npTestHistId, that.npTestHistId)
                && Objects.equals(employeeNumber, that.employeeNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(npTestHistId, employeeNumber);
    }
}
