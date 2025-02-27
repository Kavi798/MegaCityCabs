package Driver;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DriverOperations {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Add New Driver (Set Status to "Available" on Creation)
    public static int addDriver(Drivers driver) {
        String query = "INSERT INTO Drivers (dName, phone, license_number, nic, vehicle_id, dstatus) VALUES (?, ?, ?, ?, ?, 'available')";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, driver.getdName());
            stmt.setString(2, driver.getPhone());
            stmt.setString(3, driver.getLicenseNumber());
            stmt.setString(4, driver.getNic());
            stmt.setObject(5, driver.getVehicleId() > 0 ? driver.getVehicleId() : null, java.sql.Types.INTEGER);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error adding driver: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Get All Drivers
    public static List<Drivers> getAllDrivers() {
        List<Drivers> drivers = new ArrayList<>();
        String query = "SELECT * FROM Drivers";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                drivers.add(new Drivers(
                        rs.getInt("id"),
                        rs.getString("dName"),
                        rs.getString("phone"),
                        rs.getString("license_number"),
                        rs.getString("nic"),
                        rs.getInt("vehicle_id"),
                        rs.getString("dstatus")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching drivers: " + e.getMessage());
            e.printStackTrace();
        }
        return drivers;
    }

    // Delete Driver
    public static int deleteDriver(int id) {
        String query = "DELETE FROM Drivers WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting driver: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Check If Driver is Available on a Given Date
    public static boolean isDriverAvailable(int driverId, String requestedDate) {
        String query = "SELECT COUNT(*) FROM Bookings WHERE driver_id = ? AND pickup_date = ? AND bstatus IN ('pending', 'accepted')";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            stmt.setString(2, requestedDate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Driver is already booked on this date
            }
        } catch (SQLException e) {
            System.err.println("Error checking driver availability: " + e.getMessage());
            e.printStackTrace();
        }
        return true; // Driver is available
    }

    // Assign Driver to Booking (Only if Available on the Given Date)
    public static boolean assignDriverToBooking(int driverId, int bookingId, String requestedDate) {
        if (!isDriverAvailable(driverId, requestedDate)) {
            System.out.println("Driver is not available on this date.");
            return false; // Prevent assignment
        }
        String query = "UPDATE Bookings SET driver_id = ?, bstatus = 'accepted' WHERE id = ? AND pickup_date = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, bookingId);
            stmt.setString(3, requestedDate);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning driver to booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}