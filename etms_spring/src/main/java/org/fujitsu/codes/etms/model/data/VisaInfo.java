package org.fujitsu.codes.etms.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "visa_info")
@IdClass(VisaInfoId.class)
public class VisaInfo {

    @Id
    @Column(name = "emp_no", nullable = false)
    @ColumnTransformer(read = "cast(emp_no as varchar)", write = "?::integer")
    private String employeeNumber;

    @Id
    @Column(name = "visa_type_id", nullable = false)
    private Long visaTypeId;

    @Column(name = "issued")
    private LocalDate issuedDate;

    @Column(name = "expiry")
    private LocalDate expiryDate;

    @Transient
    private Long visaInfoId;

    @Transient
    private Boolean cancelFlag = Boolean.FALSE;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

    public Long getVisaInfoId() {
        return visaInfoId;
    }

    public void setVisaInfoId(Long visaInfoId) {
        this.visaInfoId = visaInfoId;
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

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(Boolean cancelFlag) {
        this.cancelFlag = cancelFlag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
