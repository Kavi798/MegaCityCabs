/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package User;
import User.Users;
import DB.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author MSII
 */
public class UserOperations {
    private static final BCryptPasswordEncoder encoder;

    static {
        BCryptPasswordEncoder tempEncoder = null;
        try {
            tempEncoder = new BCryptPasswordEncoder();
            System.out.println("BCryptPasswordEncoder initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        encoder = tempEncoder;
    }
    
    // Create - Add New User
    public static int addAccount(Users user) {
        String query = "INSERT INTO Users (email, username, password, role, name, address, phone, nic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, encoder.encode(user.getPassword()));  // Encrypt Password
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getName());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getPhone());
            stmt.setString(8, user.getNic());
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
    
    // Read - Get All Users
    public static List<Users> getAllAccounts() {
        List<Users> user = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                user.add(new Users(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password"),
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
        return user;
    }
    
    // Delete - Remove User
    public static int deleteAccount(int id) {
        String query = "DELETE FROM Users WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Validate Login
    public static Users validateLogin(String email, String rawPassword) {
        String query = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (encoder.matches(rawPassword, storedPassword)) {
                    return new Users(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("username"),
                        null,  // Do not return password for security
                        rs.getString("role"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("nic")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
