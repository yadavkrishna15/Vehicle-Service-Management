package com.vsms.service;

import com.vsms.exception.ResourceNotFoundException;
import com.vsms.model.Bill;
import com.vsms.model.ServiceRequest;
import com.vsms.repository.BillRepository;
import com.vsms.repository.ServiceRequestRepository;
import com.vsms.repository.UserRepository;
import com.vsms.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public BillService(BillRepository billRepository,
                       ServiceRequestRepository serviceRequestRepository,
                       UserRepository userRepository,
                       VehicleRepository vehicleRepository) {
        this.billRepository = billRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    private synchronized String generateNextBillId() {
        long count = billRepository.count();
        long nextNum = count + 1;
        String id;
        do {
            id = String.format("BILL-%04d", nextNum++);
        } while (billRepository.findByBillId(id).isPresent());
        return id;
    }

    public Bill createBill(Bill bill) {
        if (bill.getBillId() == null || bill.getBillId().isBlank()) {
            bill.setBillId(generateNextBillId());
        }

        // Default charges
        if (bill.getServiceCharges() == null || bill.getServiceCharges() < 0) bill.setServiceCharges(0.0);
        if (bill.getPartsCharges() == null || bill.getPartsCharges() < 0) bill.setPartsCharges(0.0);
        if (bill.getOtherCharges() == null || bill.getOtherCharges() < 0) bill.setOtherCharges(0.0);
        bill.calculateTotal();

        if (bill.getPaymentStatus() == null || bill.getPaymentStatus().isBlank()) {
            bill.setPaymentStatus("Pending");
        }
        if (bill.getBillDate() == null) {
            bill.setBillDate(new Date());
        }

        // If serviceId is provided, pull customer and vehicle info
        if (bill.getServiceId() != null && !bill.getServiceId().isBlank()) {
            serviceRequestRepository.findByServiceId(bill.getServiceId()).ifPresent(req -> {
                if (bill.getCustomerId() == null) bill.setCustomerId(req.getCustomerId());
                if (bill.getCustomerName() == null) bill.setCustomerName(req.getCustomerName());
                if (bill.getVehicleId() == null) bill.setVehicleId(req.getVehicleId());
                if (bill.getVehicleNumber() == null) bill.setVehicleNumber(req.getVehicleNumber());

                // Update final cost on service request
                req.setFinalCost(bill.getTotalAmount());
                serviceRequestRepository.save(req);
            });
        }

        // If customerId is set, enrich customerName
        if (bill.getCustomerId() != null && (bill.getCustomerName() == null || bill.getCustomerName().isBlank())) {
            userRepository.findById(bill.getCustomerId()).ifPresent(u -> bill.setCustomerName(u.getName()));
        }

        // If vehicleId is set, enrich vehicleNumber
        if (bill.getVehicleId() != null && (bill.getVehicleNumber() == null || bill.getVehicleNumber().isBlank())) {
            vehicleRepository.findById(bill.getVehicleId()).ifPresent(v -> bill.setVehicleNumber(v.getVehicleNumber()));
        }

        return billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public List<Bill> getBillsByCustomerId(String customerId) {
        return billRepository.findByCustomerId(customerId);
    }

    public Bill getBillById(String id) {
        return billRepository.findById(id)
                .or(() -> billRepository.findByBillId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + id));
    }

    public Bill updateBill(String id, Bill updated) {
        Bill existing = getBillById(id);

        if (updated.getServiceCharges() != null) existing.setServiceCharges(Math.max(0, updated.getServiceCharges()));
        if (updated.getPartsCharges() != null) existing.setPartsCharges(Math.max(0, updated.getPartsCharges()));
        if (updated.getOtherCharges() != null) existing.setOtherCharges(Math.max(0, updated.getOtherCharges()));
        existing.calculateTotal();

        if (updated.getPaymentStatus() != null) {
            existing.setPaymentStatus(updated.getPaymentStatus());
        }

        Bill saved = billRepository.save(existing);

        // Keep service request final cost in sync
        if (saved.getServiceId() != null) {
            serviceRequestRepository.findByServiceId(saved.getServiceId()).ifPresent(req -> {
                req.setFinalCost(saved.getTotalAmount());
                serviceRequestRepository.save(req);
            });
        }

        return saved;
    }

    public Bill updatePaymentStatus(String id, String status) {
        Bill existing = getBillById(id);
        existing.setPaymentStatus(status);
        return billRepository.save(existing);
    }
}
