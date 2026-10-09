package com.vsms.controller;

import com.vsms.dto.ApiResponse;
import com.vsms.model.Mechanic;
import com.vsms.service.MechanicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mechanics")
public class MechanicController {

    private final MechanicService mechanicService;

    public MechanicController(MechanicService mechanicService) {
        this.mechanicService = mechanicService;
    }

    /**
     * Add new mechanic
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Mechanic>> addMechanic(@RequestBody Mechanic mechanic) {
        Mechanic created = mechanicService.addMechanic(mechanic);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mechanic added successfully", created));
    }

    /**
     * List all mechanics
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Mechanic>>> getAllMechanics() {
        List<Mechanic> list = mechanicService.getAllMechanics();
        return ResponseEntity.ok(ApiResponse.success("Mechanics retrieved", list));
    }

    /**
     * Get single mechanic by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Mechanic>> getMechanicById(@PathVariable String id) {
        Mechanic mechanic = mechanicService.getMechanicById(id);
        return ResponseEntity.ok(ApiResponse.success("Mechanic found", mechanic));
    }

    /**
     * Update mechanic details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Mechanic>> updateMechanic(@PathVariable String id, @RequestBody Mechanic mechanic) {
        Mechanic updated = mechanicService.updateMechanic(id, mechanic);
        return ResponseEntity.ok(ApiResponse.success("Mechanic updated successfully", updated));
    }

    /**
     * Delete mechanic
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMechanic(@PathVariable String id) {
        mechanicService.deleteMechanic(id);
        return ResponseEntity.ok(ApiResponse.success("Mechanic deleted successfully"));
    }
}
