package org.fujitsu.codes.etms.model.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "np_test_hist")
public class NpTestHist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "np_test_id")
    private Long npTestHistId;

    @Column(name = "np_lvl_id")
    private Long npLvlId;

    @Transient
    private String npLvlInfoCode;

    @Column(name = "test_date")
    private LocalDate testDate;

    @Transient
    private String testCenter;

    @Transient
    private String testLevel;

    @Transient
    private Integer score;

    @Transient
    private Boolean passed;

    @Transient
    private String remarks;

    @Transient
    private LocalDateTime createdAt;

    @Transient
    private LocalDateTime updatedAt;

    public Long getNpTestHistId() {
        return npTestHistId;
    }

    public void setNpTestHistId(Long npTestHistId) {
        this.npTestHistId = npTestHistId;
    }

    public Long getNpLvlId() {
        return npLvlId;
    }

    public void setNpLvlId(Long npLvlId) {
        this.npLvlId = npLvlId;
    }

    public String getNpLvlInfoCode() {
        return npLvlInfoCode;
    }

    public void setNpLvlInfoCode(String npLvlInfoCode) {
        this.npLvlInfoCode = npLvlInfoCode;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public String getTestCenter() {
        return testCenter;
    }

    public void setTestCenter(String testCenter) {
        this.testCenter = testCenter;
    }

    public String getTestLevel() {
        return testLevel;
    }

    public void setTestLevel(String testLevel) {
        this.testLevel = testLevel;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
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
