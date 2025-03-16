package User;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserOperations {

    private static final BCryptPasswordEncoder encoder;

    static {
        BCryptPasswordEncoder tempEncoder = null;
        try {
            tempEncoder = new BCryptPasswordEncoder();
            System.out.println("✅ BCryptPasswordEncoder initialized successfully.");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize BCryptPasswordEncoder: " + e.getMessage());
            e.printStackTrace();
        }
        encoder = tempEncoder;
    }

    // ✅ Create - Add New User
    public static int addAccount(Users user) {
        if (encoder == null) {
            throw new IllegalStateException("BCryptPasswordEncoder not initialized!");
        }

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
                        null,
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

    // ✅ Get User by ID
    public static Users getUserById(int id) {
        String query = "SELECT id, email, username, role, name, address, phone, nic FROM Users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Users(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("username"),
                        null,
                        rs.getString("role"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("nic")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Delete User
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

    // ✅ Validate Login
    public static Users validateLogin(String email, String password) {
        if (encoder == null) {
            throw new IllegalStateException("BCryptPasswordEncoder not initialized!");
        }

        String query = "SELECT id, email, password, role, username, name, address, phone, nic FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                if (encoder.matches(password, storedHashedPassword)) {
                    return new Users(rs.getInt("id"), rs.getString("email"), rs.getString("username"), null, rs.getString("role"), rs.getString("name"), rs.getString("address"), rs.getString("phone"), rs.getString("nic"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Update User
    public static Users updateUser(Users user) {
        String query = "UPDATE Users SET role = ?, name = ?, address = ?, phone = ?, nic = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getRole());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getAddress());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getNic());
            stmt.setInt(6, user.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                // Fetch and return the updated user
                return getUserById(user.getId()); // ✅ Return the updated user object
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // ✅ Return null if the update fails
    }
}
