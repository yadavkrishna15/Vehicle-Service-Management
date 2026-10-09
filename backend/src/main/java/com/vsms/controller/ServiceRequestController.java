package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.model.ServiceRequest;
import com.vsms.service.ServiceRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /**
     * Book a new vehicle service request (Customer)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceRequest>> bookRequest(@RequestBody ServiceRequest request) {
        ServiceRequest created = serviceRequestService.bookRequest(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service request submitted successfully! Tracking ID: " + created.getServiceId(), created));
    }

    /**
     * Get all service requests (Admin)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceRequest>>> getAllRequests() {
        List<ServiceRequest> list = serviceRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success("All service requests retrieved", list));
    }

    /**
     * Get service requests for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<ServiceRequest>>> getRequestsByCustomer(@PathVariable String customerId) {
        List<ServiceRequest> list = serviceRequestService.getRequestsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer service requests retrieved", list));
    }

    /**
     * Search service requests by status or service ID
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ServiceRequest>>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String id) {
        List<ServiceRequest> list = serviceRequestService.search(status, id);
        return ResponseEntity.ok(ApiResponse.success("Service requests retrieved", list));
    }

    /**
     * Get single service request by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceRequest>> getRequestById(@PathVariable String id) {
        ServiceRequest req = serviceRequestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Service request found", req));
    }

    /**
     * Update full service request (Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceRequest>> updateRequest(
            @PathVariable String id,
            @RequestBody ServiceRequest request) {
        ServiceRequest updated = serviceRequestService.updateRequest(id, request);
        return ResponseEntity.ok(ApiResponse.success("Service request updated successfully", updated));
    }

    /**
     * Update service request status only
     * Supports either JSON body {"status": "Completed"} or query param ?status=Completed
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ServiceRequest>> updateStatus(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(required = false) String status) {

        String newStatus = status;
        if ((newStatus == null || newStatus.isBlank()) && body != null) {
            newStatus = body.get("status");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Status value is required");
        }

        ServiceRequest updated = serviceRequestService.updateStatus(id, newStatus.trim());
        return ResponseEntity.ok(ApiResponse.success("Status updated to: " + newStatus, updated));
    }

    /**
     * Assign mechanic to service request
     * Supports either JSON body {"mechanicId": "..."} or query param ?mechanicId=...
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<ServiceRequest>> assignMechanic(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(required = false) String mechanicId) {

        String mId = mechanicId;
        if ((mId == null || mId.isBlank()) && body != null) {
            mId = body.get("mechanicId");
        }

        ServiceRequest updated = serviceRequestService.assignMechanic(id, mId);
        return ResponseEntity.ok(ApiResponse.success("Mechanic assigned successfully", updated));
    }
}
