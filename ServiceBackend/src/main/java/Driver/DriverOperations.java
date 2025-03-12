package Driver;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverOperations {

 public static int addDriver(Drivers driver) {
        String query = "INSERT INTO Drivers (dName, phone, license_number, nic, dstatus) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, driver.getdName());
            stmt.setString(2, driver.getPhone());
            stmt.setString(3, driver.getLicenseNumber());
            stmt.setString(4, driver.getNic());
            stmt.setString(5, driver.getDstatus()); // use value from form ("available" or "busy")

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // return generated ID
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

    // Get All Available Drivers
    public static List<Drivers> getAvailableDrivers() {
        List<Drivers> availableDrivers = new ArrayList<>();
        String query = "SELECT * FROM Drivers WHERE dstatus = 'available'";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                availableDrivers.add(new Drivers(
                        rs.getInt("id"),
                        rs.getString("dName"),
                        rs.getString("phone"),
                        rs.getString("license_number"),
                        rs.getString("nic"),
                        rs.getString("dstatus")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available drivers: " + e.getMessage());
            e.printStackTrace();
        }
        return availableDrivers;
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

    // ✅ Assign Driver to Booking and Mark as Busy
    public static boolean assignDriverToBooking(int driverId, int bookingId, String requestedDate) {
        if (!isDriverAvailable(driverId, requestedDate)) {
            System.out.println("Driver is not available on this date.");
            return false;
        }

        String query = "UPDATE Bookings SET driver_id = ?, bstatus = 'accepted' WHERE id = ? AND pickup_date = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, bookingId);
            stmt.setString(3, requestedDate);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                updateDriverStatusToBusy(driverId); // ✅ Set driver as busy
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error assigning driver to booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // (Optional) Get Assigned Vehicle for Driver -- IF YOU ADD VEHICLES LATER
    public static String getDriverVehicleDetails(int driverId) {
        // Optional feature if you add vehicle management in future
        return "{}";
    }

    public static Drivers getDriverById(int id) {
        String query = "SELECT * FROM Drivers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Drivers(
                        rs.getInt("id"),
                        rs.getString("dName"),
                        rs.getString("phone"),
                        rs.getString("license_number"),
                        rs.getString("nic"),
                        rs.getString("dstatus")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching driver by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Update Driver Method
    public static boolean updateDriver(Drivers driver) {
        String query = "UPDATE Drivers SET dName=?, phone=?, license_number=?, nic=?, dstatus=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, driver.getdName());
            stmt.setString(2, driver.getPhone());
            stmt.setString(3, driver.getLicenseNumber());
            stmt.setString(4, driver.getNic());
            stmt.setString(5, driver.getDstatus());
            stmt.setInt(6, driver.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // return true if update happened

        } catch (SQLException e) {
            System.err.println("Error updating driver: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Update Driver Status to Busy
    public static boolean updateDriverStatusToBusy(int driverId) {
        String query = "UPDATE Drivers SET dstatus = 'busy' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, driverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating driver status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
