package product;

import db.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class ProductManager {
    private Connection connection;
    private Scanner scanner;
    private int sellerId;

    public ProductManager(int sellerId) {
        this.connection = DBConnection.getConnection();
        this.scanner = new Scanner(System.in);
        this.sellerId = sellerId;
    }

    /**
     * Main product management menu
     */
    public void showMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════╗");
            System.out.println("║     PRODUCT MANAGEMENT         ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.println("1. View All Products");
            System.out.println("2. Add New Product");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Search Product");
            System.out.println("6. View Statistics");
            System.out.println("7. Logout");
            System.out.print("\nChoose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllProducts();
                        break;
                    case 2:
                        addProduct();
                        break;
                    case 3:
                        updateProduct();
                        break;
                    case 4:
                        deleteProduct();
                        break;
                    case 5:
                        searchProduct();
                        break;
                    case 6:
                        viewStatistics();
                        break;
                    case 7:
                        System.out.println("\n✅ Logged out successfully!");
                        return;
                    default:
                        System.out.println("\n❌ Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n❌ Please enter a valid number!");
            }
        }
    }

    /**
     * View all products
     */
    public void viewAllProducts() {
        System.out.println("\n========== YOUR PRODUCTS ==========");
        try {
            String sql = "SELECT * FROM products WHERE seller_id = ? ORDER BY product_id DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println(String.format("%-5s %-20s %-15s %-10s %-10s %-10s", 
                "ID", "Name", "Category", "Price", "Discount", "Stock"));
            System.out.println("─".repeat(80));

            boolean hasProducts = false;
            while (rs.next()) {
                hasProducts = true;
                System.out.println(String.format("%-5d %-20s %-15s $%-9.2f %-9.0f%% %-10d",
                    rs.getInt("product_id"),
                    truncate(rs.getString("name"), 20),
                    truncate(rs.getString("category"), 15),
                    rs.getDouble("price"),
                    rs.getDouble("discount"),
                    rs.getInt("stock")
                ));
            }

            if (!hasProducts) {
                System.out.println("No products found. Add your first product!");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching products: " + e.getMessage());
        }
    }

    /**
     * Add new product
     */
    public void addProduct() {
        System.out.println("\n========== ADD NEW PRODUCT ==========");
        
        try {
            System.out.print("Product Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Category: ");
            String category = scanner.nextLine().trim();
            
            System.out.print("Price: $");
            double price = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Discount (%): ");
            double discount = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Stock Quantity: ");
            int stock = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            String sql = "INSERT INTO products (name, category, price, discount, stock, description, seller_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, price);
            stmt.setDouble(4, discount);
            stmt.setInt(5, stock);
            stmt.setString(6, description);
            stmt.setInt(7, sellerId);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                System.out.println("\n✅ Product added successfully!");
            } else {
                System.out.println("\n❌ Failed to add product!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Invalid number format!");
        } catch (SQLException e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
        }
    }

    /**
     * Update product
     */
    public void updateProduct() {
        System.out.println("\n========== UPDATE PRODUCT ==========");
        System.out.print("Enter Product ID to update: ");
        
        try {
            int productId = Integer.parseInt(scanner.nextLine());

            // Check if product exists
            String checkSql = "SELECT * FROM products WHERE product_id = ? AND seller_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, productId);
            checkStmt.setInt(2, sellerId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("\n❌ Product not found!");
                rs.close();
                checkStmt.close();
                return;
            }

            // Display current values
            System.out.println("\nCurrent Product Details:");
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Category: " + rs.getString("category"));
            System.out.println("Price: $" + rs.getDouble("price"));
            System.out.println("Discount: " + rs.getDouble("discount") + "%");
            System.out.println("Stock: " + rs.getInt("stock"));
            
            rs.close();
            checkStmt.close();

            // Get new values
            System.out.println("\n--- Enter new values (press Enter to keep current) ---");
            
            System.out.print("New Price: $");
            String priceInput = scanner.nextLine().trim();
            
            System.out.print("New Discount (%): ");
            String discountInput = scanner.nextLine().trim();
            
            System.out.print("New Stock: ");
            String stockInput = scanner.nextLine().trim();
            
            System.out.print("New Description: ");
            String descInput = scanner.nextLine().trim();

            // Build update query
            StringBuilder sql = new StringBuilder("UPDATE products SET ");
            boolean hasUpdate = false;

            if (!priceInput.isEmpty()) {
                sql.append("price = ?, ");
                hasUpdate = true;
            }
            if (!discountInput.isEmpty()) {
                sql.append("discount = ?, ");
                hasUpdate = true;
            }
            if (!stockInput.isEmpty()) {
                sql.append("stock = ?, ");
                hasUpdate = true;
            }
            if (!descInput.isEmpty()) {
                sql.append("description = ?, ");
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("\n⚠️ No changes made!");
                return;
            }

            // Remove last comma and add WHERE clause
            sql.setLength(sql.length() - 2);
            sql.append(" WHERE product_id = ? AND seller_id = ?");

            PreparedStatement stmt = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (!priceInput.isEmpty()) {
                stmt.setDouble(paramIndex++, Double.parseDouble(priceInput));
            }
            if (!discountInput.isEmpty()) {
                stmt.setDouble(paramIndex++, Double.parseDouble(discountInput));
            }
            if (!stockInput.isEmpty()) {
                stmt.setInt(paramIndex++, Integer.parseInt(stockInput));
            }
            if (!descInput.isEmpty()) {
                stmt.setString(paramIndex++, descInput);
            }

            stmt.setInt(paramIndex++, productId);
            stmt.setInt(paramIndex, sellerId);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                System.out.println("\n✅ Product updated successfully!");
            } else {
                System.out.println("\n❌ Failed to update product!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Invalid number format!");
        } catch (SQLException e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
        }
    }

    /**
     * Delete product
     */
    public void deleteProduct() {
        System.out.println("\n========== DELETE PRODUCT ==========");
        System.out.print("Enter Product ID to delete: ");
        
        try {
            int productId = Integer.parseInt(scanner.nextLine());

            System.out.print("Are you sure you want to delete this product? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (!confirm.equals("yes")) {
                System.out.println("\n⚠️ Deletion cancelled!");
                return;
            }

            String sql = "DELETE FROM products WHERE product_id = ? AND seller_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, productId);
            stmt.setInt(2, sellerId);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                System.out.println("\n✅ Product deleted successfully!");
            } else {
                System.out.println("\n❌ Product not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Invalid product ID!");
        } catch (SQLException e) {
            System.err.println("❌ Error deleting product: " + e.getMessage());
        }
    }

    /**
     * Search product
     */
    public void searchProduct() {
        System.out.println("\n========== SEARCH PRODUCT ==========");
        System.out.print("Enter product name or category: ");
        String keyword = scanner.nextLine().trim();

        try {
            String sql = "SELECT * FROM products WHERE seller_id = ? AND " +
                        "(name LIKE ? OR category LIKE ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            System.out.println(String.format("\n%-5s %-20s %-15s %-10s %-10s %-10s", 
                "ID", "Name", "Category", "Price", "Discount", "Stock"));
            System.out.println("─".repeat(80));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(String.format("%-5d %-20s %-15s $%-9.2f %-9.0f%% %-10d",
                    rs.getInt("product_id"),
                    truncate(rs.getString("name"), 20),
                    truncate(rs.getString("category"), 15),
                    rs.getDouble("price"),
                    rs.getDouble("discount"),
                    rs.getInt("stock")
                ));
            }

            if (!found) {
                System.out.println("No products found matching '" + keyword + "'");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error searching products: " + e.getMessage());
        }
    }

    /**
     * View statistics
     */
    public void viewStatistics() {
        System.out.println("\n========== PRODUCT STATISTICS ==========");
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_products, " +
                        "SUM(stock) as total_stock, " +
                        "AVG(price) as avg_price, " +
                        "SUM(CASE WHEN stock > 10 THEN 1 ELSE 0 END) as in_stock, " +
                        "SUM(CASE WHEN stock > 0 AND stock <= 10 THEN 1 ELSE 0 END) as low_stock, " +
                        "SUM(CASE WHEN stock = 0 THEN 1 ELSE 0 END) as out_of_stock, " +
                        "SUM(CASE WHEN discount > 0 THEN 1 ELSE 0 END) as with_discount " +
                        "FROM products WHERE seller_id = ?";
            
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Total Products: " + rs.getInt("total_products"));
                System.out.println("Total Stock Units: " + rs.getInt("total_stock"));
                System.out.println("Average Price: $" + String.format("%.2f", rs.getDouble("avg_price")));
                System.out.println("In Stock (>10): " + rs.getInt("in_stock"));
                System.out.println("Low Stock (1-10): " + rs.getInt("low_stock"));
                System.out.println("Out of Stock: " + rs.getInt("out_of_stock"));
                System.out.println("Products with Discount: " + rs.getInt("with_discount"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching statistics: " + e.getMessage());
        }
    }

    /**
     * Utility method to truncate long strings
     */
    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    public void close() {
        scanner.close();
    }
}