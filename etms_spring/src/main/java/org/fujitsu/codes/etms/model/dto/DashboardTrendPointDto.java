package org.fujitsu.codes.etms.model.dto;

public class DashboardTrendPointDto {

    private String label;
    private long value;

    public DashboardTrendPointDto() {
    }

    public DashboardTrendPointDto(String label, long value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
