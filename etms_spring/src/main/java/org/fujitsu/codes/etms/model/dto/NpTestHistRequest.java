package org.fujitsu.codes.etms.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NpTestHistRequest {

    @NotBlank(message = "NP level info code is required")
    @Size(max = 30, message = "NP level info code must be at most 30 characters")
    private String npLvlInfoCode;

    private LocalDate testDate;

    @Size(max = 150, message = "Test center must be at most 150 characters")
    private String testCenter;

    @Size(max = 50, message = "Test level must be at most 50 characters")
    private String testLevel;

    private Integer score;
    private Boolean passed;
    @Size(max = 255, message = "Remarks must be at most 255 characters")
    private String remarks;

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
}