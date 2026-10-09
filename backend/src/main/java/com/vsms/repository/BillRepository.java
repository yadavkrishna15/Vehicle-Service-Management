package com.vsms.repository;

import com.vsms.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {

    Optional<Bill> findByBillId(String billId);

    List<Bill> findByCustomerId(String customerId);

    Optional<Bill> findByServiceId(String serviceId);

    List<Bill> findByVehicleId(String vehicleId);

    boolean existsByBillId(String billId);
}
