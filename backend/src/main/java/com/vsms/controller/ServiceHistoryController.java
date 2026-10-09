package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.model.ServiceHistory;
import com.vsms.service.ServiceHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class ServiceHistoryController {

    private final ServiceHistoryService serviceHistoryService;

    public ServiceHistoryController(ServiceHistoryService serviceHistoryService) {
        this.serviceHistoryService = serviceHistoryService;
    }

    /**
     * Get service history for a specific vehicle
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<ServiceHistory>>> getHistoryByVehicle(@PathVariable String vehicleId) {
        List<ServiceHistory> list = serviceHistoryService.getHistoryByVehicleId(vehicleId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle service history retrieved", list));
    }

    /**
     * Get service history for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<ServiceHistory>>> getHistoryByCustomer(@PathVariable String customerId) {
        List<ServiceHistory> list = serviceHistoryService.getHistoryByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer service history retrieved", list));
    }

    /**
     * Create manual service history entry
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceHistory>> createHistory(@RequestBody ServiceHistory history) {
        ServiceHistory created = serviceHistoryService.createHistory(history);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service history recorded", created));
    }
}
