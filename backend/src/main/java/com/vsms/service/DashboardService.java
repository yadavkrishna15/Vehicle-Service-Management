package com.vsms.service;

import com.vsms.repository.BillRepository;
import com.vsms.repository.ServiceRequestRepository;
import com.vsms.repository.UserRepository;
import com.vsms.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final BillRepository billRepository;

    public DashboardService(UserRepository userRepository,
                            VehicleRepository vehicleRepository,
                            ServiceRequestRepository serviceRequestRepository,
                            BillRepository billRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.billRepository = billRepository;
    }

    /**
     * Aggregated statistics for Admin Dashboard.
     */
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalCustomers = userRepository.countByRole("CUSTOMER");
        long totalVehicles = vehicleRepository.count();
        long totalServiceRequests = serviceRequestRepository.count();

        // Pending services include 'Requested' and 'Confirmed'
        long pendingServices = serviceRequestRepository.countByStatusIgnoreCaseIn(List.of("Requested", "Confirmed"));
        long inProgressServices = serviceRequestRepository.countByStatusIgnoreCase("In Service");
        long completedServices = serviceRequestRepository.countByStatusIgnoreCase("Completed");
        long totalBills = billRepository.count();

        stats.put("totalCustomers", totalCustomers);
        stats.put("totalVehicles", totalVehicles);
        stats.put("totalServiceRequests", totalServiceRequests);
        stats.put("pendingServices", pendingServices);
        stats.put("inProgressServices", inProgressServices);
        stats.put("completedServices", completedServices);
        stats.put("totalBills", totalBills);

        return stats;
    }

    /**
     * Aggregated statistics for Customer Dashboard.
     */
    public Map<String, Object> getCustomerStats(String customerId) {
        Map<String, Object> stats = new HashMap<>();

        long customerVehicles = vehicleRepository.findByCustomerId(customerId).size();
        var requests = serviceRequestRepository.findByCustomerId(customerId);
        long activeServices = requests.stream()
                .filter(r -> !"Completed".equalsIgnoreCase(r.getStatus()) && !"Cancelled".equalsIgnoreCase(r.getStatus()))
                .count();
        long completedServices = requests.stream()
                .filter(r -> "Completed".equalsIgnoreCase(r.getStatus()))
                .count();
        long totalBills = billRepository.findByCustomerId(customerId).size();

        stats.put("customerVehicles", customerVehicles);
        stats.put("activeServices", activeServices);
        stats.put("completedServices", completedServices);
        stats.put("totalBills", totalBills);

        return stats;
    }
}
