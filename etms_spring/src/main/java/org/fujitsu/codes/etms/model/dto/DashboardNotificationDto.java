package org.fujitsu.codes.etms.model.dto;

public class DashboardNotificationDto {

    private String type;
    private String title;
    private String message;
    private String route;
    private String severity;

    public DashboardNotificationDto() {
    }

    public DashboardNotificationDto(String type, String title, String message, String route, String severity) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.route = route;
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
