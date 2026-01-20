package auth;

import db.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class SellerLogin {
    private Connection connection;
    private Scanner scanner;

    public SellerLogin() {
        this.connection = DBConnection.getConnection();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Login method
     */
    public int login() {
        System.out.println("\n========== SELLER LOGIN ==========");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            String sql = "SELECT seller_id, username FROM sellers WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int sellerId = rs.getInt("seller_id");
                String sellerName = rs.getString("username");
                
                System.out.println("\n✅ Login Successful!");
                System.out.println("Welcome, " + sellerName + "!");
                
                rs.close();
                stmt.close();
                return sellerId;
            } else {
                System.out.println("\n❌ Invalid username or password!");
                rs.close();
                stmt.close();
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Register new seller
     */
    public boolean register() {
        System.out.println("\n========== SELLER REGISTRATION ==========");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            // Check if username exists
            String checkSql = "SELECT seller_id FROM sellers WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n❌ Username already exists!");
                rs.close();
                checkStmt.close();
                return false;
            }
            rs.close();
            checkStmt.close();

            // Insert new seller
            String sql = "INSERT INTO sellers (username, password) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                System.out.println("\n✅ Registration successful! You can now login.");
                return true;
            } else {
                System.out.println("\n❌ Registration failed!");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Main menu for login/register
     */
    public int showLoginMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════╗");
            System.out.println("║   E-COMMERCE SELLER SYSTEM     ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("\nChoose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        int sellerId = login();
                        if (sellerId != -1) {
                            return sellerId;
                        }
                        break;
                    case 2:
                        register();
                        break;
                    case 3:
                        System.out.println("\n👋 Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("\n❌ Invalid option! Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n❌ Please enter a valid number!");
            }
        }
    }

    public void close() {
        scanner.close();
    }
}