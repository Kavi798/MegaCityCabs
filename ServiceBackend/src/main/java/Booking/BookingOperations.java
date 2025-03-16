package Booking;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingOperations {

    public static int createBooking(Bookings booking) {
        try (Connection conn = DBConnection.getConnection()) {
            // ✅ First, check if driver and vehicle are available
            int driverId = getAvailableDriverId();
            int vehicleId = getAvailableVehicleId(booking.getVehicleType());

            // ❌ If either driver or vehicle is not available, return -1 (customer will see error)
            if (driverId == -1 || vehicleId == -1) {
                return -1;
            }

            // ✅ Proceed to create booking as 'pending' without assigning driver/vehicle yet
            String query = "INSERT INTO Bookings (user_id, pickup_location, dropoff_location, fare, bstatus, pickup_date, vehicle_type) "
                    + "VALUES (?, ?, ?, ?, 'pending', ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                double fare = calculateFare(booking.getVehicleType()); // ✅ Fare calculated internally

                stmt.setInt(1, booking.getUserId());
                stmt.setString(2, booking.getPickupLocation());
                stmt.setString(3, booking.getDropoffLocation());
                stmt.setDouble(4, fare);
                stmt.setDate(5, Date.valueOf(booking.getPickupDate()));
                stmt.setString(6, booking.getVehicleType());

                // ✅ Execute and retrieve generated booking ID
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating booking failed, no rows affected.");
                }

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return generated booking ID
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 on failure
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
                        rs.getString("pickup_date"), // ✅
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
                        rs.getString("pickup_date"),
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
        try (Connection conn = DBConnection.getConnection()) {
            if (status.equalsIgnoreCase("accepted")) {
                // Step 1: Find vehicle type for the booking
                String getBookingQuery = "SELECT vehicle_type FROM Bookings WHERE id = ?";
                String vehicleType = null;
                try (PreparedStatement stmt = conn.prepareStatement(getBookingQuery)) {
                    stmt.setInt(1, bookingId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        vehicleType = rs.getString("vehicle_type");
                    } else {
                        return false; // Booking not found
                    }
                }

                // Step 2: Find available driver and vehicle
                int driverId = getAvailableDriverId();
                int vehicleId = getAvailableVehicleId(vehicleType);

                // ✅ Even if not available, admin accepts — driver/vehicle can be added later manually
                String updateQuery = "UPDATE Bookings SET bstatus = ?, driver_id = ?, vehicle_id = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setString(1, "accepted");
                    stmt.setObject(2, driverId == -1 ? null : driverId); // NULL if not found
                    stmt.setObject(3, vehicleId == -1 ? null : vehicleId); // NULL if not found
                    stmt.setInt(4, bookingId);
                    return stmt.executeUpdate() > 0;
                }

            } else {
                // ✅ For completed or cancelled
                String query = "UPDATE Bookings SET bstatus = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, status);
                    stmt.setInt(2, bookingId);
                    return stmt.executeUpdate() > 0;
                }
            }

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

    public static List<Bookings> getBookingsByUser(int userId) {
        List<Bookings> bookings = new ArrayList<>();
        String query = "SELECT * FROM Bookings WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
                        rs.getString("pickup_date"),
                        rs.getString("vehicle_type")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user bookings: " + e.getMessage());
        }
        return bookings;
    }

    public static double calculateFare(String vehicleType) {
        switch (vehicleType.toLowerCase()) {
            case "sedan":
                return 200 + (50 * 10); // 10km as default distance
            case "suv":
                return 300 + (60 * 10);
            case "luxury":
                return 500 + (80 * 10);
            case "van":
                return 400 + (70 * 10);
            default:
                return 0.0;
        }
    }

    public static boolean cancelBooking(int bookingId) {
        String query = "UPDATE Bookings SET bstatus = 'cancelled' WHERE id = ? AND bstatus = 'pending'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean autoAssignDriverAndVehicle(int bookingId, String vehicleType) {
        String findDriverQuery = "SELECT id FROM Drivers WHERE dstatus = 'available' LIMIT 1";
        String findVehicleQuery = "SELECT id FROM Vehicles WHERE type = ? AND status = 'available' LIMIT 1";
        String updateBookingQuery = "UPDATE Bookings SET driver_id = ?, vehicle_id = ?, bstatus = 'accepted' WHERE id = ?";
        String updateDriverQuery = "UPDATE Drivers SET dstatus = 'busy' WHERE id = ?";
        String updateVehicleQuery = "UPDATE Vehicles SET status = 'unavailable' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            Integer driverId = null;
            Integer vehicleId = null;

            // Step 1: Find available driver
            try (PreparedStatement driverStmt = conn.prepareStatement(findDriverQuery); ResultSet driverRs = driverStmt.executeQuery()) {
                if (driverRs.next()) {
                    driverId = driverRs.getInt("id");
                }
            }

            // Step 2: Find available vehicle by type
            try (PreparedStatement vehicleStmt = conn.prepareStatement(findVehicleQuery)) {
                vehicleStmt.setString(1, vehicleType);
                try (ResultSet vehicleRs = vehicleStmt.executeQuery()) {
                    if (vehicleRs.next()) {
                        vehicleId = vehicleRs.getInt("id");
                    }
                }
            }

            // Step 3: Assign if both found
            if (driverId != null && vehicleId != null) {
                // Update Booking
                try (PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingQuery)) {
                    updateBookingStmt.setInt(1, driverId);
                    updateBookingStmt.setInt(2, vehicleId);
                    updateBookingStmt.setInt(3, bookingId);
                    updateBookingStmt.executeUpdate();
                }

                // Update Driver status to busy
                try (PreparedStatement updateDriverStmt = conn.prepareStatement(updateDriverQuery)) {
                    updateDriverStmt.setInt(1, driverId);
                    updateDriverStmt.executeUpdate();
                }

                // Update Vehicle status to unavailable
                try (PreparedStatement updateVehicleStmt = conn.prepareStatement(updateVehicleQuery)) {
                    updateVehicleStmt.setInt(1, vehicleId);
                    updateVehicleStmt.executeUpdate();
                }

                conn.commit(); // Commit Transaction
                return true;
            } else {
                conn.rollback(); // Rollback if not assigned
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Get available driver ID
    public static int getAvailableDriverId() {
        String query = "SELECT id FROM Drivers WHERE dstatus = 'available' LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available driver: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // No available driver found
    }

// ✅ Get available vehicle ID based on type
    public static int getAvailableVehicleId(String vehicleType) {
        String query = "SELECT id FROM Vehicles WHERE type = ? AND status = 'available' LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vehicleType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // No available vehicle found
    }
}
