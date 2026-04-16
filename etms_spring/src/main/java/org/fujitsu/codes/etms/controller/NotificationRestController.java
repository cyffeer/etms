package org.fujitsu.codes.etms.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.model.dto.DashboardNotificationDto;
import org.fujitsu.codes.etms.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final DashboardService dashboardService;

    public NotificationRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<ApiResponse<?>> getNotifications(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "100") Integer limit) {

        List<DashboardNotificationDto> filtered = dashboardService.getNotifications().stream()
                .filter(notification -> matchesSeverity(notification, severity))
                .filter(notification -> matchesType(notification, type))
                .limit(limit == null || limit <= 0 ? 100 : limit)
                .toList();

        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully", filtered));
        }
        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());
        List<DashboardNotificationDto> data = filtered.subList(fromIndex, toIndex);
        long totalElements = filtered.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    private boolean matchesSeverity(DashboardNotificationDto notification, String severity) {
        if (severity == null || severity.isBlank()) {
            return true;
        }
        return notification.getSeverity() != null
                && notification.getSeverity().toLowerCase(Locale.ROOT).equals(severity.trim().toLowerCase(Locale.ROOT));
    }

    private boolean matchesType(DashboardNotificationDto notification, String type) {
        if (type == null || type.isBlank()) {
            return true;
        }
        return notification.getType() != null
                && notification.getType().toLowerCase(Locale.ROOT).contains(type.trim().toLowerCase(Locale.ROOT));
    }
}
