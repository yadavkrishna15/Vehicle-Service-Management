package com.vsms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "bills")
public class Bill {

    @Id
    private String id;

    @Indexed(unique = true)
    private String billId; // e.g. BILL-0001

    private String serviceId;  // ref service_requests.serviceId
    private String customerId; // ref users._id
    private String customerName; // helper for display

    private String vehicleId;  // ref vehicles._id
    private String vehicleNumber; // helper for display

    private Double serviceCharges;
    private Double partsCharges;
    private Double otherCharges;
    private Double totalAmount;

    private String paymentStatus; // Pending, Paid
    private Date billDate;

    public Bill() {
        this.serviceCharges = 0.0;
        this.partsCharges = 0.0;
        this.otherCharges = 0.0;
        this.totalAmount = 0.0;
        this.paymentStatus = "Pending";
        this.billDate = new Date();
    }

    public void calculateTotal() {
        double sc = this.serviceCharges != null ? this.serviceCharges : 0.0;
        double pc = this.partsCharges != null ? this.partsCharges : 0.0;
        double oc = this.otherCharges != null ? this.otherCharges : 0.0;
        this.totalAmount = sc + pc + oc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
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

    public Double getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(Double serviceCharges) {
        this.serviceCharges = serviceCharges;
        calculateTotal();
    }

    public Double getPartsCharges() {
        return partsCharges;
    }

    public void setPartsCharges(Double partsCharges) {
        this.partsCharges = partsCharges;
        calculateTotal();
    }

    public Double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(Double otherCharges) {
        this.otherCharges = otherCharges;
        calculateTotal();
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }
}
