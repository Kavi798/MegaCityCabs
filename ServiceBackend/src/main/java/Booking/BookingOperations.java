package Booking;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingOperations {

    public static int createBooking(Bookings booking) {
        String query = "INSERT INTO Bookings (user_id, pickup_location, dropoff_location, fare, bstatus, pickup_date, vehicle_type) "
                + "VALUES (?, ?, ?, ?, 'pending', ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setString(2, booking.getPickupLocation());
            stmt.setString(3, booking.getDropoffLocation());
            stmt.setDouble(4, booking.getFare());
            stmt.setDate(5, booking.getPickupDate());
            stmt.setString(6, booking.getVehicleType());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Bookings> getAllBookings() {
        List<Bookings> bookings = new ArrayList<>();
        String query = "SELECT b.*, u.name AS customer_name "
                + "FROM Bookings b "
                + "JOIN Users u ON b.user_id = u.id "
                + "WHERE u.role = 'cus'";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Integer driverId = rs.getObject("driver_id") != null ? rs.getInt("driver_id") : null;
                Integer vehicleId = rs.getObject("vehicle_id") != null ? rs.getInt("vehicle_id") : null;
                Bookings booking = new Bookings(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        driverId,
                        vehicleId,
                        rs.getString("pickup_location"),
                        rs.getString("dropoff_location"),
                        rs.getDouble("fare"),
                        rs.getString("bstatus"),
                        rs.getTimestamp("created_at"),
                        rs.getDate("pickup_date"), // ✅
                        rs.getString("vehicle_type") // ✅
                );
                booking.setCustomerName(rs.getString("customer_name"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    // Accept Booking (Assign Driver & Vehicle)
    public static boolean acceptBooking(int bookingId, int driverId, int vehicleId) {
        String query = "UPDATE Bookings SET driver_id = ?, vehicle_id = ?, bstatus = 'accepted' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, vehicleId);
            stmt.setInt(3, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error accepting booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Complete Booking
    public static boolean completeBooking(int bookingId) {
        String query = "UPDATE Bookings SET bstatus = 'completed' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error completing booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Bill Generation Method: Get Booking Details + Customer Name for Billing
    public static Bookings getBookingDetailsWithCustomer(int bookingId) {
        String query = "SELECT b.*, u.name AS customer_name, u.address, u.phone "
                + "FROM Bookings b "
                + "JOIN Users u ON b.user_id = u.id "
                + "WHERE b.id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Bookings booking = new Bookings(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        (Integer) rs.getObject("driver_id"),
                        (Integer) rs.getObject("vehicle_id"),
                        rs.getString("pickup_location"),
                        rs.getString("dropoff_location"),
                        rs.getDouble("fare"),
                        rs.getString("bstatus"),
                        rs.getTimestamp("created_at"),
                        rs.getDate("pickup_date"),
                        rs.getString("vehicle_type")
                );
                booking.setCustomerName(rs.getString("customer_name"));
                booking.setCustomerAddress(rs.getString("address"));
                booking.setCustomerPhone(rs.getString("phone"));
                return booking;
            }
        } catch (SQLException e) {
            System.err.println("Error generating bill: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateBookingStatus(int bookingId, String status) {
        String query = "UPDATE Bookings SET bstatus = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Assign Vehicle to Booking (with status check)
    public static boolean assignVehicleToBooking(int bookingId, int vehicleId) {
        String checkStatusQuery = "SELECT bstatus FROM Bookings WHERE id = ?";
        String assignVehicleQuery = "UPDATE Bookings SET vehicle_id = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // Step 1: Check current booking status
            try (PreparedStatement checkStmt = conn.prepareStatement(checkStatusQuery)) {
                checkStmt.setInt(1, bookingId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    String status = rs.getString("bstatus");
                    // ❌ Block if status is 'completed' or 'cancelled'
                    if (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("cancelled")) {
                        return false; // Cannot assign vehicle if completed/cancelled
                    }
                } else {
                    return false; // Booking not found
                }
            }

            // Step 2: If valid, assign vehicle
            try (PreparedStatement assignStmt = conn.prepareStatement(assignVehicleQuery)) {
                assignStmt.setInt(1, vehicleId);
                assignStmt.setInt(2, bookingId);
                return assignStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error assigning vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
