package com.vsms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "mechanics")
public class Mechanic {

    @Id
    private String id;

    @Indexed(unique = true)
    private String staffId; // e.g. MEC-001

    private String name;
    private String phone;
    private String specialization;
    private String availability; // Available, Busy

    private Date createdAt;

    public Mechanic() {
        this.availability = "Available";
        this.createdAt = new Date();
    }

    public Mechanic(String staffId, String name, String phone, String specialization, String availability) {
        this.staffId = staffId;
        this.name = name;
        this.phone = phone;
        this.specialization = specialization;
        this.availability = availability != null ? availability : "Available";
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
