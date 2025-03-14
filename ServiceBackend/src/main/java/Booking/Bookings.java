package Booking;

import java.sql.Timestamp;
import java.sql.Date; // Import Date properly

public class Bookings {

    private int id;
    private int userId;
    private Integer driverId;
    private Integer vehicleId;
    private String pickupLocation;
    private String dropoffLocation;
    private double fare;
    private String bstatus;
    private Timestamp createdAt;
    private String  pickupDate;    // java.sql.Date
    private String vehicleType;

    // ✅ Extra fields for Billing
    private String customerName;
    private String customerAddress;
    private String customerPhone;

    // Constructors
    public Bookings() {
    }

    public Bookings(int id, int userId, Integer driverId, Integer vehicleId, String pickupLocation, String dropoffLocation,
            double fare, String bstatus, Timestamp createdAt, String pickupDate, String vehicleType) {
        this.id = id;
        this.userId = userId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.fare = fare;
        this.bstatus = bstatus;
        this.createdAt = createdAt;
        this.pickupDate = pickupDate;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getBstatus() {
        return bstatus;
    }

    public void setBstatus(String bstatus) {
        this.bstatus = bstatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String  getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String  pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
