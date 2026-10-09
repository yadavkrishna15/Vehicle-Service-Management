package com.vsms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "service_requests")
public class ServiceRequest {

    @Id
    private String id;

    @Indexed(unique = true)
    private String serviceId; // e.g. SRV-0001

    private String customerId; // ref users._id
    private String customerName; // helper for display

    private String vehicleId;  // ref vehicles._id
    private String vehicleNumber; // helper for display

    private String serviceType; // General Service, Oil Change, Brake Service, Engine Check, Battery Check, Tyre Service, Washing, Other
    private String problemDescription;
    private Date preferredDate;
    private String status; // Requested, Confirmed, In Service, Completed, Cancelled

    private String assignedMechanicId; // ref mechanics._id
    private String assignedMechanicName; // helper for display

    private Double estimatedCost;
    private Double finalCost;

    private Date createdAt;

    public ServiceRequest() {
        this.status = "Requested";
        this.createdAt = new Date();
        this.estimatedCost = 0.0;
        this.finalCost = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public Date getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(Date preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedMechanicId() {
        return assignedMechanicId;
    }

    public void setAssignedMechanicId(String assignedMechanicId) {
        this.assignedMechanicId = assignedMechanicId;
    }

    public String getAssignedMechanicName() {
        return assignedMechanicName;
    }

    public void setAssignedMechanicName(String assignedMechanicName) {
        this.assignedMechanicName = assignedMechanicName;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(Double finalCost) {
        this.finalCost = finalCost;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
