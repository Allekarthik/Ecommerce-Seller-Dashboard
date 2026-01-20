package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database credentials from your MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce_dashboard";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    private static Connection connection = null;

    /**
     * Get database connection (Singleton pattern)
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Create connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection!");
            e.printStackTrace();
        }
    }

    /**
     * Test connection
     */
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("✅ Connection test successful!");
            closeConnection();
        } else {
            System.out.println("❌ Connection test failed!");
        }
    }
}
