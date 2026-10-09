package com.vsms.repository;

import com.vsms.model.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    boolean existsByVehicleNumber(String vehicleNumber);

    List<Vehicle> findByCustomerId(String customerId);

    List<Vehicle> findByVehicleNumberContainingIgnoreCase(String vehicleNumber);
}
