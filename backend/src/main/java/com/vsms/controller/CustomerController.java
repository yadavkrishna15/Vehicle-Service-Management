package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.model.User;
import com.vsms.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * List all registered customers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllCustomers() {
        List<User> list = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", list));
    }

    /**
     * Search customers by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchByName(@RequestParam(required = false, defaultValue = "") String name) {
        List<User> list = customerService.searchByName(name);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved", list));
    }

    /**
     * Get single customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getCustomerById(@PathVariable String id) {
        User user = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer found", user));
    }

    /**
     * Update customer details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateCustomer(@PathVariable String id, @RequestBody User user) {
        User updated = customerService.updateCustomer(id, user);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updated));
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully"));
    }
}
