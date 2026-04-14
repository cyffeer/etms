package org.fujitsu.codes.etms.model.data;

import java.io.Serializable;
import java.util.Objects;

public class TrngHistId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long trngHistId;
    private String employeeNumber;

    public TrngHistId() {
    }

    public TrngHistId(Long trngHistId, String employeeNumber) {
        this.trngHistId = trngHistId;
        this.employeeNumber = employeeNumber;
    }

    public Long getTrngHistId() {
        return trngHistId;
    }

    public void setTrngHistId(Long trngHistId) {
        this.trngHistId = trngHistId;
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
        if (!(o instanceof TrngHistId that)) return false;
        return Objects.equals(trngHistId, that.trngHistId)
                && Objects.equals(employeeNumber, that.employeeNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trngHistId, employeeNumber);
    }
}
