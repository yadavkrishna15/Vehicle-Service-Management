package com.vsms.repository;

import com.vsms.model.ServiceHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceHistoryRepository extends MongoRepository<ServiceHistory, String> {

    List<ServiceHistory> findByVehicleId(String vehicleId);

    List<ServiceHistory> findByCustomerId(String customerId);

    Optional<ServiceHistory> findByServiceId(String serviceId);
}
