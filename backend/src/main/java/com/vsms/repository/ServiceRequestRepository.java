package com.vsms.repository;

import com.vsms.model.ServiceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends MongoRepository<ServiceRequest, String> {

    Optional<ServiceRequest> findByServiceId(String serviceId);

    List<ServiceRequest> findByCustomerId(String customerId);

    List<ServiceRequest> findByVehicleId(String vehicleId);

    List<ServiceRequest> findByStatusIgnoreCase(String status);

    List<ServiceRequest> findByServiceIdContainingIgnoreCase(String serviceId);

    long countByStatusIgnoreCase(String status);

    long countByStatusIgnoreCaseIn(List<String> statuses);
}
