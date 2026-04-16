package org.fujitsu.codes.etms.model.dto;

import java.util.List;

public class EmployeeEventSummaryResponse {

    private long totalCount;
    private long activeCount;
    private long pendingCount;
    private long closedCount;
    private List<EmployeeEventSummaryItem> categories;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(long activeCount) {
        this.activeCount = activeCount;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getClosedCount() {
        return closedCount;
    }

    public void setClosedCount(long closedCount) {
        this.closedCount = closedCount;
    }

    public List<EmployeeEventSummaryItem> getCategories() {
        return categories;
    }

    public void setCategories(List<EmployeeEventSummaryItem> categories) {
        this.categories = categories;
    }
}
