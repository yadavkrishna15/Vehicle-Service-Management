package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.model.Bill;
import com.vsms.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    /**
     * Generate / create a new bill
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Bill>> createBill(@RequestBody Bill bill) {
        Bill created = billService.createBill(bill);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill generated successfully! Invoice ID: " + created.getBillId(), created));
    }

    /**
     * List all bills (Admin view)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Bill>>> getAllBills() {
        List<Bill> list = billService.getAllBills();
        return ResponseEntity.ok(ApiResponse.success("All bills retrieved", list));
    }

    /**
     * List bills for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Bill>>> getBillsByCustomer(@PathVariable String customerId) {
        List<Bill> list = billService.getBillsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer bills retrieved", list));
    }

    /**
     * Get single bill by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Bill>> getBillById(@PathVariable String id) {
        Bill bill = billService.getBillById(id);
        return ResponseEntity.ok(ApiResponse.success("Bill found", bill));
    }

    /**
     * Update bill charges
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Bill>> updateBill(@PathVariable String id, @RequestBody Bill bill) {
        Bill updated = billService.updateBill(id, bill);
        return ResponseEntity.ok(ApiResponse.success("Bill updated successfully", updated));
    }

    /**
     * Update bill payment status (Pending / Paid)
     */
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<ApiResponse<Bill>> updatePaymentStatus(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(required = false) String status) {

        String newStatus = status;
        if ((newStatus == null || newStatus.isBlank()) && body != null) {
            newStatus = body.get("status");
            if (newStatus == null) newStatus = body.get("paymentStatus");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Payment status is required (Pending or Paid)");
        }

        Bill updated = billService.updatePaymentStatus(id, newStatus.trim());
        return ResponseEntity.ok(ApiResponse.success("Payment status updated to " + newStatus, updated));
    }
}
