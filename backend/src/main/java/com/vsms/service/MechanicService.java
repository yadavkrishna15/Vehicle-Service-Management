package com.vsms.service;

import com.vsms.exception.ResourceNotFoundException;
import com.vsms.model.Mechanic;
import com.vsms.repository.MechanicRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MechanicService {

    private final MechanicRepository mechanicRepository;

    public MechanicService(MechanicRepository mechanicRepository) {
        this.mechanicRepository = mechanicRepository;
    }

    private synchronized String generateNextStaffId() {
        long count = mechanicRepository.count();
        long nextNum = count + 1;
        String id;
        do {
            id = String.format("MEC-%03d", nextNum++);
        } while (mechanicRepository.findByStaffId(id).isPresent());
        return id;
    }

    public Mechanic addMechanic(Mechanic mechanic) {
        if (mechanic.getName() == null || mechanic.getName().isBlank()) {
            throw new IllegalArgumentException("Mechanic name is required");
        }
        if (mechanic.getStaffId() == null || mechanic.getStaffId().isBlank()) {
            mechanic.setStaffId(generateNextStaffId());
        }
        if (mechanic.getAvailability() == null || mechanic.getAvailability().isBlank()) {
            mechanic.setAvailability("Available");
        }
        if (mechanic.getCreatedAt() == null) {
            mechanic.setCreatedAt(new Date());
        }
        return mechanicRepository.save(mechanic);
    }

    public List<Mechanic> getAllMechanics() {
        return mechanicRepository.findAll();
    }

    public Mechanic getMechanicById(String id) {
        return mechanicRepository.findById(id)
                .or(() -> mechanicRepository.findByStaffId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found: " + id));
    }

    public Mechanic updateMechanic(String id, Mechanic updated) {
        Mechanic existing = getMechanicById(id);

        if (updated.getName() != null) existing.setName(updated.getName().trim());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone().trim());
        if (updated.getSpecialization() != null) existing.setSpecialization(updated.getSpecialization().trim());
        if (updated.getAvailability() != null) existing.setAvailability(updated.getAvailability().trim());

        return mechanicRepository.save(existing);
    }

    public void deleteMechanic(String id) {
        Mechanic m = getMechanicById(id);
        mechanicRepository.deleteById(m.getId());
    }
}
