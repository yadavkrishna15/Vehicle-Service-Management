package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get dashboard summary statistics.
     * If customerId is provided, returns customer-scoped stats.
     * Otherwise returns overall admin stats.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @RequestParam(required = false) String customerId) {
        Map<String, Object> stats;
        if (customerId != null && !customerId.isBlank()) {
            stats = dashboardService.getCustomerStats(customerId);
        } else {
            stats = dashboardService.getAdminStats();
        }
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved", stats));
    }
}
