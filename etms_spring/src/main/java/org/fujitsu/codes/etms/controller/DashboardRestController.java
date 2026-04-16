package org.fujitsu.codes.etms.controller;

import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.fujitsu.codes.etms.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final DashboardService dashboardService;

    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<ApiResponse<?>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary fetched successfully", dashboardService.buildSummary()));
    }
}
