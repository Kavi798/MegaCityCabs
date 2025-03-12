package Reports;

import DB.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsOperations {

    // Get Total Bookings
    public static int getTotalBookings() {
        String query = "SELECT COUNT(*) AS total FROM Bookings";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Get Total Revenue (Completed bookings only)
    public static double getTotalRevenue() {
        String query = "SELECT SUM(fare) AS total_revenue FROM Bookings WHERE bstatus = 'completed'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
}
