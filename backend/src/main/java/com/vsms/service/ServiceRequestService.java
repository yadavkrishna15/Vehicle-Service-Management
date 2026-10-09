package com.vsms.service;

import com.vsms.exception.ResourceNotFoundException;
import com.vsms.model.*;
import com.vsms.repository.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final MechanicRepository mechanicRepository;
    private final ServiceHistoryRepository serviceHistoryRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 VehicleRepository vehicleRepository,
                                 UserRepository userRepository,
                                 MechanicRepository mechanicRepository,
                                 ServiceHistoryRepository serviceHistoryRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.mechanicRepository = mechanicRepository;
        this.serviceHistoryRepository = serviceHistoryRepository;
    }

    /**
     * Generate sequential serviceId in format SRV-0001
     */
    private synchronized String generateNextServiceId() {
        long count = serviceRequestRepository.count();
        long nextNum = count + 1;
        String id;
        do {
            id = String.format("SRV-%04d", nextNum++);
        } while (serviceRequestRepository.findByServiceId(id).isPresent());
        return id;
    }

    public ServiceRequest bookRequest(ServiceRequest request) {
        request.setServiceId(generateNextServiceId());
        request.setStatus("Requested");
        request.setCreatedAt(new Date());

        // Populate customer and vehicle metadata for easy UI rendering
        if (request.getCustomerId() != null) {
            userRepository.findById(request.getCustomerId()).ifPresent(user -> {
                request.setCustomerName(user.getName());
            });
        }

        if (request.getVehicleId() != null) {
            vehicleRepository.findById(request.getVehicleId()).ifPresent(vehicle -> {
                request.setVehicleNumber(vehicle.getVehicleNumber());
            });
        }

        if (request.getEstimatedCost() == null) {
            request.setEstimatedCost(0.0);
        }
        if (request.getFinalCost() == null) {
            request.setFinalCost(0.0);
        }

        return serviceRequestRepository.save(request);
    }

    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepository.findAll();
    }

    public List<ServiceRequest> getRequestsByCustomerId(String customerId) {
        return serviceRequestRepository.findByCustomerId(customerId);
    }

    public ServiceRequest getRequestById(String id) {
        // Can search by MongoDB _id or serviceId (e.g. SRV-0001)
        return serviceRequestRepository.findById(id)
                .or(() -> serviceRequestRepository.findByServiceId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found: " + id));
    }

    public ServiceRequest updateRequest(String id, ServiceRequest updated) {
        ServiceRequest existing = getRequestById(id);

        if (updated.getServiceType() != null) existing.setServiceType(updated.getServiceType());
        if (updated.getProblemDescription() != null) existing.setProblemDescription(updated.getProblemDescription());
        if (updated.getPreferredDate() != null) existing.setPreferredDate(updated.getPreferredDate());
        if (updated.getEstimatedCost() != null) existing.setEstimatedCost(updated.getEstimatedCost());
        if (updated.getFinalCost() != null) existing.setFinalCost(updated.getFinalCost());

        if (updated.getAssignedMechanicId() != null) {
            assignMechanic(existing.getId(), updated.getAssignedMechanicId());
        }

        if (updated.getStatus() != null && !updated.getStatus().equalsIgnoreCase(existing.getStatus())) {
            updateStatus(existing.getId(), updated.getStatus());
        }

        return serviceRequestRepository.save(existing);
    }

    public ServiceRequest updateStatus(String id, String newStatus) {
        ServiceRequest existing = getRequestById(id);
        existing.setStatus(newStatus);
        ServiceRequest saved = serviceRequestRepository.save(existing);

        // Auto Service History Trigger: When status is marked "Completed"
        if ("Completed".equalsIgnoreCase(newStatus)) {
            triggerAutoServiceHistory(saved);
        }

        return saved;
    }

    public ServiceRequest assignMechanic(String id, String mechanicId) {
        ServiceRequest existing = getRequestById(id);

        if (mechanicId == null || mechanicId.isBlank()) {
            existing.setAssignedMechanicId(null);
            existing.setAssignedMechanicName(null);
        } else {
            Mechanic mechanic = mechanicRepository.findById(mechanicId)
                    .or(() -> mechanicRepository.findByStaffId(mechanicId))
                    .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found: " + mechanicId));

            existing.setAssignedMechanicId(mechanic.getId());
            existing.setAssignedMechanicName(mechanic.getName());

            // Update mechanic status to Busy
            mechanic.setAvailability("Busy");
            mechanicRepository.save(mechanic);

            // If status was 'Requested', advance to 'Confirmed'
            if ("Requested".equalsIgnoreCase(existing.getStatus())) {
                existing.setStatus("Confirmed");
            }
        }

        return serviceRequestRepository.save(existing);
    }

    private void triggerAutoServiceHistory(ServiceRequest req) {
        // Check if history already created for this service
        if (serviceHistoryRepository.findByServiceId(req.getServiceId()).isEmpty()) {
            ServiceHistory history = new ServiceHistory();
            history.setVehicleId(req.getVehicleId());
            history.setVehicleNumber(req.getVehicleNumber());
            history.setCustomerId(req.getCustomerId());
            history.setServiceId(req.getServiceId());
            history.setServiceDate(new Date());
            history.setServiceType(req.getServiceType());
            history.setProblem(req.getProblemDescription());
            history.setWorkPerformed("Completed full maintenance for " + req.getServiceType());
            history.setMechanicName(req.getAssignedMechanicName() != null ? req.getAssignedMechanicName() : "General Staff");
            double cost = (req.getFinalCost() != null && req.getFinalCost() > 0)
                    ? req.getFinalCost()
                    : (req.getEstimatedCost() != null ? req.getEstimatedCost() : 0.0);
            history.setCost(cost);
            history.setStatus("Completed");

            serviceHistoryRepository.save(history);
        }

        // Release mechanic availability back to Available
        if (req.getAssignedMechanicId() != null) {
            mechanicRepository.findById(req.getAssignedMechanicId()).ifPresent(m -> {
                m.setAvailability("Available");
                mechanicRepository.save(m);
            });
        }
    }

    public List<ServiceRequest> search(String status, String searchId) {
        if (searchId != null && !searchId.isBlank()) {
            return serviceRequestRepository.findByServiceIdContainingIgnoreCase(searchId.trim());
        }
        if (status != null && !status.isBlank()) {
            return serviceRequestRepository.findByStatusIgnoreCase(status.trim());
        }
        return serviceRequestRepository.findAll();
    }
}
