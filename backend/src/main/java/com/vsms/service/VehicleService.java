package com.vsms.service;

import com.vsms.exception.ResourceNotFoundException;
import com.vsms.model.User;
import com.vsms.model.Vehicle;
import com.vsms.repository.UserRepository;
import com.vsms.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicle.getVehicleNumber() == null || vehicle.getVehicleNumber().isBlank()) {
            throw new IllegalArgumentException("Vehicle number is required");
        }
        String formattedNumber = vehicle.getVehicleNumber().trim().toUpperCase();
        vehicle.setVehicleNumber(formattedNumber);

        if (vehicleRepository.existsByVehicleNumber(formattedNumber)) {
            throw new IllegalArgumentException("Vehicle with number '" + formattedNumber + "' is already registered.");
        }

        // If customer details are missing, populate from customerId
        if (vehicle.getCustomerId() != null && !vehicle.getCustomerId().isBlank()) {
            userRepository.findById(vehicle.getCustomerId()).ifPresent(user -> {
                if (vehicle.getCustomerName() == null || vehicle.getCustomerName().isBlank()) {
                    vehicle.setCustomerName(user.getName());
                }
                if (vehicle.getCustomerContact() == null || vehicle.getCustomerContact().isBlank()) {
                    vehicle.setCustomerContact(user.getPhone());
                }
            });
        }

        if (vehicle.getCreatedAt() == null) {
            vehicle.setCreatedAt(new Date());
        }

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getVehiclesByCustomerId(String customerId) {
        return vehicleRepository.findByCustomerId(customerId);
    }

    public Vehicle getVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    public Vehicle updateVehicle(String id, Vehicle updated) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (updated.getVehicleType() != null) existing.setVehicleType(updated.getVehicleType());
        if (updated.getBrand() != null) existing.setBrand(updated.getBrand());
        if (updated.getModel() != null) existing.setModel(updated.getModel());
        if (updated.getManufacturingYear() != null) existing.setManufacturingYear(updated.getManufacturingYear());
        if (updated.getFuelType() != null) existing.setFuelType(updated.getFuelType());
        if (updated.getCustomerName() != null) existing.setCustomerName(updated.getCustomerName());
        if (updated.getCustomerContact() != null) existing.setCustomerContact(updated.getCustomerContact());

        return vehicleRepository.save(existing);
    }

    public void deleteVehicle(String id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> searchByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumberContainingIgnoreCase(vehicleNumber.trim());
    }
}
