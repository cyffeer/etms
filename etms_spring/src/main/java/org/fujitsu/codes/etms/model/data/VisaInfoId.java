package org.fujitsu.codes.etms.model.data;

import java.io.Serializable;
import java.util.Objects;

public class VisaInfoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String employeeNumber;
    private Long visaTypeId;

    public VisaInfoId() {
    }

    public VisaInfoId(String employeeNumber, Long visaTypeId) {
        this.employeeNumber = employeeNumber;
        this.visaTypeId = visaTypeId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getVisaTypeId() {
        return visaTypeId;
    }

    public void setVisaTypeId(Long visaTypeId) {
        this.visaTypeId = visaTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisaInfoId that)) return false;
        return Objects.equals(employeeNumber, that.employeeNumber)
                && Objects.equals(visaTypeId, that.visaTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNumber, visaTypeId);
    }
}
