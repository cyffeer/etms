package org.fujitsu.codes.etms.model.dto;

public class DashboardCardDto {

    private String label;
    private long count;
    private String route;

    public DashboardCardDto() {
    }

    public DashboardCardDto(String label, long count, String route) {
        this.label = label;
        this.count = count;
        this.route = route;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
