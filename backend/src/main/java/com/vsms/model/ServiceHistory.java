package com.vsms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "service_history")
public class ServiceHistory {

    @Id
    private String id;

    private String vehicleId;
    private String vehicleNumber; // helper for display
    private String customerId;    // ref users._id (supports customer query)

    private String serviceId;
    private Date serviceDate;
    private String serviceType;
    private String problem;
    private String workPerformed;
    private String mechanicName;
    private Double cost;
    private String status;

    public ServiceHistory() {
        this.serviceDate = new Date();
    }

    public ServiceHistory(String vehicleId, String vehicleNumber, String customerId,
                          String serviceId, Date serviceDate, String serviceType,
                          String problem, String workPerformed, String mechanicName,
                          Double cost, String status) {
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.serviceDate = serviceDate != null ? serviceDate : new Date();
        this.serviceType = serviceType;
        this.problem = problem;
        this.workPerformed = workPerformed;
        this.mechanicName = mechanicName;
        this.cost = cost;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getWorkPerformed() {
        return workPerformed;
    }

    public void setWorkPerformed(String workPerformed) {
        this.workPerformed = workPerformed;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
