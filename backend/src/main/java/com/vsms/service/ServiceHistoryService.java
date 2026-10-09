package com.vsms.service;

import com.vsms.model.ServiceHistory;
import com.vsms.repository.ServiceHistoryRepository;
import com.vsms.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ServiceHistoryService {

    private final ServiceHistoryRepository serviceHistoryRepository;
    private final VehicleRepository vehicleRepository;

    public ServiceHistoryService(ServiceHistoryRepository serviceHistoryRepository,
                                 VehicleRepository vehicleRepository) {
        this.serviceHistoryRepository = serviceHistoryRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<ServiceHistory> getHistoryByVehicleId(String vehicleId) {
        return serviceHistoryRepository.findByVehicleId(vehicleId);
    }

    public List<ServiceHistory> getHistoryByCustomerId(String customerId) {
        return serviceHistoryRepository.findByCustomerId(customerId);
    }

    public ServiceHistory createHistory(ServiceHistory history) {
        if (history.getServiceDate() == null) {
            history.setServiceDate(new Date());
        }
        if (history.getVehicleId() != null && (history.getVehicleNumber() == null || history.getVehicleNumber().isBlank())) {
            vehicleRepository.findById(history.getVehicleId()).ifPresent(v -> {
                history.setVehicleNumber(v.getVehicleNumber());
                if (history.getCustomerId() == null) {
                    history.setCustomerId(v.getCustomerId());
                }
            });
        }
        return serviceHistoryRepository.save(history);
    }
}
