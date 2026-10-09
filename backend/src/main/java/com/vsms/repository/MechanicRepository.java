package com.vsms.repository;

import com.vsms.model.Mechanic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MechanicRepository extends MongoRepository<Mechanic, String> {

    Optional<Mechanic> findByStaffId(String staffId);

    boolean existsByStaffId(String staffId);

    List<Mechanic> findByAvailabilityIgnoreCase(String availability);
}
