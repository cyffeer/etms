package org.fujitsu.codes.etms.model.dto;

import java.util.List;

public class DashboardSummaryResponse {

    private List<DashboardCardDto> cards;
    private List<DashboardTrendPointDto> trainingsPerMonth;
    private List<DashboardTrendPointDto> attendanceTrends;
    private List<DashboardNotificationDto> notifications;

    public List<DashboardCardDto> getCards() {
        return cards;
    }

    public void setCards(List<DashboardCardDto> cards) {
        this.cards = cards;
    }

    public List<DashboardTrendPointDto> getTrainingsPerMonth() {
        return trainingsPerMonth;
    }

    public void setTrainingsPerMonth(List<DashboardTrendPointDto> trainingsPerMonth) {
        this.trainingsPerMonth = trainingsPerMonth;
    }

    public List<DashboardTrendPointDto> getAttendanceTrends() {
        return attendanceTrends;
    }

    public void setAttendanceTrends(List<DashboardTrendPointDto> attendanceTrends) {
        this.attendanceTrends = attendanceTrends;
    }

    public List<DashboardNotificationDto> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<DashboardNotificationDto> notifications) {
        this.notifications = notifications;
    }
}
