package User;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserOperations {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    static {
        try {
            System.out.println("BCryptPasswordEncoder initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Create - Add New User
    public static int addAccount(Users user) {
        String query = "INSERT INTO Users (email, username, password, role, name, address, phone, nic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, encoder.encode(user.getPassword()));  // ✅ Encrypt Password
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getName());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getPhone());
            stmt.setString(8, user.getNic());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("User creation failed, no rows affected.");
            }

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ✅ Read - Get All Users
    public static List<Users> getAllAccounts() {
        List<Users> users = new ArrayList<>();
        String query = "SELECT id, email, username, role, name, address, phone, nic FROM Users";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new Users(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("username"),
                        null, // ✅ Do not return password for security
                        rs.getString("role"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("nic")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // ✅ Delete - Remove User
    public static int deleteAccount(int id) {
        String query = "DELETE FROM Users WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ✅ Validate Login with Password Hashing
    public static Users validateLogin(String email, String password) {
        String query = "SELECT id, email, password, role, username, name, address, phone, nic FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                System.out.println("🔍 Debug: User Found - Email: " + email);
                System.out.println("🔍 Debug: Stored Hashed Password: " + storedHashedPassword);
                System.out.println("🔍 Debug: Entered Password: " + password);

                // Compare raw input password with stored hashed password
                if (encoder.matches(password, storedHashedPassword)) {
                    System.out.println("✅ Debug: Password Matched! Authentication Successful.");
                    return new Users(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("username"),
                            null, // Do not return password
                            rs.getString("role"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("nic")
                    );
                } else {
                    System.out.println("❌ Debug: Password does NOT match!");
                }
            } else {
                System.out.println("❌ Debug: No user found with email: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateUser(Users user) {
        String query = "UPDATE Users SET role = ?, name = ?, address = ?, phone = ?, nic = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getRole());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getAddress());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getNic());
            stmt.setInt(6, user.getId()); // Last parameter is ID

            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows Updated: " + rowsUpdated); // ✅ Debugging log
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
