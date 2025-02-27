/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author MSII
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mega_city_cabs?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";  
    private static final String PASSWORD = "12345678";  

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Ensure MySQL driver is loaded
            System.out.println("MySQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found!", e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connected Successfully!");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting to the database.");
        }
    }

    public static void main(String[] args) {
        getConnection(); // Test the connection
    }
}
