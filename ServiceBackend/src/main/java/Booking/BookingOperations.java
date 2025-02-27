package Booking;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingOperations {

    // Create a New Booking (User Requests a Ride)
    public static int createBooking(Bookings booking) {
        String query = "INSERT INTO Bookings (user_id, pickup_location, dropoff_location, fare, status) VALUES (?, ?, ?, ?, 'pending')";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setString(2, booking.getPickupLocation());
            stmt.setString(3, booking.getDropoffLocation());
            stmt.setDouble(4, booking.getFare());
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

    // Get All Bookings
    public static List<Bookings> getAllBookings() {
        List<Bookings> bookings = new ArrayList<>();
        String query = "SELECT * FROM Bookings";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                bookings.add(new Bookings(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        (Integer) rs.getObject("driver_id"),
                        (Integer) rs.getObject("vehicle_id"),
                        rs.getString("pickup_location"),
                        rs.getString("dropoff_location"),
                        rs.getDouble("fare"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    // Accept Booking (Assign Driver & Vehicle)
    public static boolean acceptBooking(int bookingId, int driverId, int vehicleId) {
        String query = "UPDATE Bookings SET driver_id = ?, vehicle_id = ?, status = 'accepted' WHERE id = ?";
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
        String query = "UPDATE Bookings SET status = 'completed' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error completing booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
