package Vehicle;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleOperations {

    // ✅ Add Vehicle
    public static int addVehicle(Vehicles vehicle) {
        String query = "INSERT INTO Vehicles (model, plate_number, capacity, type, status, driver_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getModel());
            stmt.setString(2, vehicle.getPlateNumber());
            stmt.setInt(3, vehicle.getCapacity());
            stmt.setString(4, vehicle.getType());
            stmt.setString(5, vehicle.getStatus());
            stmt.setObject(6, vehicle.getDriverId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ✅ Get All Vehicles with Driver Name
    public static List<Vehicles> getAllVehicles() {
        List<Vehicles> vehicles = new ArrayList<>();
        String query = "SELECT v.*, d.dName AS driver_name FROM Vehicles v LEFT JOIN Drivers d ON v.driver_id = d.id";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                vehicles.add(new Vehicles(
                        rs.getInt("id"), rs.getString("model"), rs.getString("plate_number"),
                        rs.getInt("capacity"), (Integer) rs.getObject("driver_id"),
                        rs.getString("type"), rs.getString("status"), rs.getString("driver_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // ✅ Get Available Vehicles by Type
    public static List<Vehicles> getAvailableVehiclesByType(String type) {
        List<Vehicles> vehicles = new ArrayList<>();
        String query = "SELECT v.*, d.dName AS driver_name FROM Vehicles v LEFT JOIN Drivers d ON v.driver_id = d.id WHERE v.type = ? AND v.status = 'available'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicles(
                        rs.getInt("id"), rs.getString("model"), rs.getString("plate_number"),
                        rs.getInt("capacity"), (Integer) rs.getObject("driver_id"),
                        rs.getString("type"), rs.getString("status"), rs.getString("driver_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // ✅ Search Vehicles
    public static List<Vehicles> searchVehicles(String type, String status, String plateNumber) {
        List<Vehicles> vehicles = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT v.*, d.dName AS driver_name FROM Vehicles v LEFT JOIN Drivers d ON v.driver_id = d.id WHERE 1=1");

        if (type != null) {
            query.append(" AND v.type = ?");
        }
        if (status != null) {
            query.append(" AND v.status = ?");
        }
        if (plateNumber != null) {
            query.append(" AND v.plate_number LIKE ?");
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            int index = 1;
            if (type != null) {
                stmt.setString(index++, type);
            }
            if (status != null) {
                stmt.setString(index++, status);
            }
            if (plateNumber != null) {
                stmt.setString(index, "%" + plateNumber + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicles(
                        rs.getInt("id"), rs.getString("model"), rs.getString("plate_number"),
                        rs.getInt("capacity"), (Integer) rs.getObject("driver_id"),
                        rs.getString("type"), rs.getString("status"), rs.getString("driver_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public static boolean isVehicleAssigned(int vehicleId) {
        String query = "SELECT driver_id FROM Vehicles WHERE id = ? AND driver_id IS NOT NULL";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If there is a driver assigned, prevent delete
        } catch (SQLException e) {
            System.err.println("Error checking if vehicle is assigned: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Get Available Vehicles
    public static List<Vehicles> getAvailableVehicles() {
        List<Vehicles> vehicles = new ArrayList<>();
        String query = "SELECT v.*, d.dName AS driver_name FROM Vehicles v LEFT JOIN Drivers d ON v.driver_id = d.id WHERE v.status = 'available'";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                vehicles.add(new Vehicles(
                        rs.getInt("id"), rs.getString("model"), rs.getString("plate_number"),
                        rs.getInt("capacity"), (Integer) rs.getObject("driver_id"),
                        rs.getString("type"), rs.getString("status"), rs.getString("driver_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // ✅ Assign Vehicle and Driver to Booking and Set Status (Vehicle -> unavailable, Driver -> busy)
    public static boolean assignVehicleAndDriverToBooking(int vehicleId, int driverId, int bookingId) {
        String assignBooking = "UPDATE Bookings SET vehicle_id = ?, driver_id = ?, bstatus = 'accepted' WHERE id = ?";
        String updateVehicle = "UPDATE Vehicles SET status = 'unavailable', driver_id = ? WHERE id = ?";
        String updateDriver = "UPDATE Drivers SET dstatus = 'busy' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction Start

            try (PreparedStatement stmtBooking = conn.prepareStatement(assignBooking); PreparedStatement stmtVehicle = conn.prepareStatement(updateVehicle); PreparedStatement stmtDriver = conn.prepareStatement(updateDriver)) {

                stmtBooking.setInt(1, vehicleId);
                stmtBooking.setInt(2, driverId);
                stmtBooking.setInt(3, bookingId);
                stmtBooking.executeUpdate();

                stmtVehicle.setInt(1, driverId);
                stmtVehicle.setInt(2, vehicleId);
                stmtVehicle.executeUpdate();

                stmtDriver.setInt(1, driverId);
                stmtDriver.executeUpdate();

                conn.commit(); // Transaction Commit
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Rollback on Error
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete Vehicle (Only If Not Assigned to a Driver)
    public static int deleteVehicle(int id) {
        if (isVehicleAssigned(id)) {
            System.out.println("Cannot delete vehicle: It is currently assigned to a driver.");
            return -1;
        }

        String query = "DELETE FROM Vehicles WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate(); // Returns number of rows affected
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // ✅ Update Vehicle
    public static boolean updateVehicle(int id, Vehicles vehicle) {
        String query = "UPDATE Vehicles SET model = ?, plate_number = ?, capacity = ?, type = ?, status = ?, driver_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vehicle.getModel());
            stmt.setString(2, vehicle.getPlateNumber());
            stmt.setInt(3, vehicle.getCapacity());
            stmt.setString(4, vehicle.getType());
            stmt.setString(5, vehicle.getStatus());
            stmt.setObject(6, vehicle.getDriverId());
            stmt.setInt(7, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Get Vehicle by ID
    public static Vehicles getVehicleById(int id) {
        String query = "SELECT v.*, d.dName AS driver_name FROM Vehicles v LEFT JOIN Drivers d ON v.driver_id = d.id WHERE v.id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Vehicles(
                        rs.getInt("id"), rs.getString("model"), rs.getString("plate_number"),
                        rs.getInt("capacity"), (Integer) rs.getObject("driver_id"),
                        rs.getString("type"), rs.getString("status"), rs.getString("driver_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
