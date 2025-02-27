package Vehicle;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleOperations {

    // Add New Vehicle
    public static int addVehicle(Vehicles vehicle) {
        String query = "INSERT INTO Vehicles (model, plate_number, capacity, type, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getModel());
            stmt.setString(2, vehicle.getPlateNumber());
            stmt.setInt(3, vehicle.getCapacity());
            stmt.setString(4, vehicle.getType());
            stmt.setString(5, vehicle.getStatus());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Get All Vehicles
    public static List<Vehicles> getAllVehicles() {
        List<Vehicles> vehicles = new ArrayList<>();
        String query = "SELECT * FROM Vehicles";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                vehicles.add(new Vehicles(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("plate_number"),
                        rs.getInt("capacity"),
                        rs.getString("type"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    // Get All Available Vehicles (Not Assigned to Any Driver)
    public static List<Vehicles> getAvailableVehicles() {
        List<Vehicles> vehicles = new ArrayList<>();
        String query = "SELECT * FROM Vehicles WHERE id NOT IN (SELECT vehicle_id FROM Drivers WHERE vehicle_id IS NOT NULL)";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                vehicles.add(new Vehicles(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("plate_number"),
                        rs.getInt("capacity"),
                        rs.getString("type"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    // Search Vehicles by Type, Status, or Plate Number
    public static List<Vehicles> searchVehicles(String type, String status, String plateNumber) {
        List<Vehicles> vehicles = new ArrayList<>();
        String query = "SELECT * FROM Vehicles WHERE 1=1";  // Start with a flexible query

        if (type != null) {
            query += " AND type = ?";
        }
        if (status != null) {
            query += " AND status = ?";
        }
        if (plateNumber != null) {
            query += " AND plate_number LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            if (type != null) {
                stmt.setString(paramIndex++, type);
            }
            if (status != null) {
                stmt.setString(paramIndex++, status);
            }
            if (plateNumber != null) {
                stmt.setString(paramIndex++, "%" + plateNumber + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicles(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("plate_number"),
                        rs.getInt("capacity"),
                        rs.getString("type"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        return vehicles;
    }

    // Prevent Deleting Assigned Vehicles
    public static boolean isVehicleAssigned(int vehicleId) {
        String query = "SELECT id FROM Drivers WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if vehicle is assigned
        } catch (SQLException e) {
            System.err.println("Error checking if vehicle is assigned: " + e.getMessage());
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
}
