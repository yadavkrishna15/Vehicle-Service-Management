package com.vsms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "vehicles")
public class Vehicle {

    @Id
    private String id;

    @Indexed(unique = true)
    private String vehicleNumber;

    private String vehicleType; // Car, Bike, Scooter, Other
    private String brand;
    private String model;
    private Integer manufacturingYear;
    private String fuelType; // Petrol, Diesel, Electric, Hybrid, CNG

    private String customerId;     // Reference to users._id
    private String customerName;
    private String customerContact;

    private Date createdAt;

    public Vehicle() {
        this.createdAt = new Date();
    }

    public Vehicle(String vehicleNumber, String vehicleType, String brand, String model,
                   Integer manufacturingYear, String fuelType, String customerId,
                   String customerName, String customerContact) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.manufacturingYear = manufacturingYear;
        this.fuelType = fuelType;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber != null ? vehicleNumber.trim().toUpperCase() : null;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(Integer manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
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

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
