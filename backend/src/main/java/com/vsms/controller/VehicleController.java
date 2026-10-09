package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.model.Vehicle;
import com.vsms.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Add a new vehicle (Customer or Admin)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Vehicle>> addVehicle(@RequestBody Vehicle vehicle) {
        Vehicle created = vehicleService.addVehicle(vehicle);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle registered successfully", created));
    }

    /**
     * List all registered vehicles (Admin view)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Vehicle>>> getAllVehicles() {
        List<Vehicle> list = vehicleService.getAllVehicles();
        return ResponseEntity.ok(ApiResponse.success("All vehicles retrieved", list));
    }

    /**
     * List all vehicles belonging to a specific customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getVehiclesByCustomer(@PathVariable String customerId) {
        List<Vehicle> list = vehicleService.getVehiclesByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer vehicles retrieved", list));
    }

    /**
     * Search vehicles by registration number
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Vehicle>>> searchByVehicleNumber(@RequestParam(required = false, defaultValue = "") String vehicleNumber) {
        List<Vehicle> list = vehicleService.searchByVehicleNumber(vehicleNumber);
        return ResponseEntity.ok(ApiResponse.success("Vehicles matching search retrieved", list));
    }

    /**
     * Get single vehicle by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Vehicle>> getVehicleById(@PathVariable String id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle found", vehicle));
    }

    /**
     * Update vehicle details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Vehicle>> updateVehicle(@PathVariable String id, @RequestBody Vehicle vehicle) {
        Vehicle updated = vehicleService.updateVehicle(id, vehicle);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", updated));
    }

    /**
     * Delete vehicle record
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully"));
    }
}
