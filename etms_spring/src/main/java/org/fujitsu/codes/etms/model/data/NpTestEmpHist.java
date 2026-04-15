package org.fujitsu.codes.etms.model.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "np_test_emp_hist")
@IdClass(NpTestEmpHistId.class)
public class NpTestEmpHist {

    @Id
    @Column(name = "np_test_id", nullable = false)
    private Long npTestHistId;

    @Id
    @Column(name = "emp_no", nullable = false)
    @ColumnTransformer(read = "cast(emp_no as varchar)", write = "?::integer")
    private String employeeNumber;

    @Column(name = "pass_flag", length = 1)
    @ColumnTransformer(
            read = "CASE WHEN pass_flag = 'Y' THEN true WHEN pass_flag = 'N' THEN false ELSE null END",
            write = "CASE WHEN ? THEN 'Y' ELSE 'N' END")
    private Boolean passFlag;

    @Column(name = "take_flag", length = 1)
    @ColumnTransformer(
            read = "CASE WHEN take_flag = 'Y' THEN true WHEN take_flag = 'N' THEN false ELSE null END",
            write = "CASE WHEN ? THEN 'Y' ELSE 'N' END")
    private Boolean takeFlag;

    @Column(name = "points")
    private Integer points;

    @Column(name = "np_test_emp_hist_id", insertable = false, updatable = false)
    private Long npTestEmpHistId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getNpTestEmpHistId() {
        return npTestEmpHistId;
    }

    public void setNpTestEmpHistId(Long npTestEmpHistId) {
        this.npTestEmpHistId = npTestEmpHistId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getNpTestHistId() {
        return npTestHistId;
    }

    public void setNpTestHistId(Long npTestHistId) {
        this.npTestHistId = npTestHistId;
    }

    public Boolean getPassFlag() {
        return passFlag;
    }

    public void setPassFlag(Boolean passFlag) {
        this.passFlag = passFlag;
    }

    public Boolean getTakeFlag() {
        return takeFlag;
    }

    public void setTakeFlag(Boolean takeFlag) {
        this.takeFlag = takeFlag;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
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
