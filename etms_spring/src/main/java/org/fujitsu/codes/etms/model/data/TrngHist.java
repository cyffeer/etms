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
@Table(name = "trng_hist")
@IdClass(TrngHistId.class)
public class TrngHist {

    @Id
    @Column(name = "trng_id", nullable = false)
    private Long trngHistId;

    @Id
    @Column(name = "emp_no", nullable = false)
    @ColumnTransformer(read = "cast(emp_no as varchar)", write = "?::integer")
    private String employeeNumber;

    @Transient
    private String trngCode;

    @Transient
    private LocalDate completedDate;

    @Transient
    private LocalDate expiryDate;

    @Transient
    private String result;

    @Transient
    private String remarks;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

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

    public String getTrngCode() {
        return trngCode;
    }

    public void setTrngCode(String trngCode) {
        this.trngCode = trngCode;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
