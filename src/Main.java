import auth.SellerLogin;
import product.ProductManager;
import db.DBConnection;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  E-COMMERCE SELLER DASHBOARD SYSTEM   ║");
        System.out.println("║         Console Version v1.0          ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // Test database connection
        if (DBConnection.getConnection() == null) {
            System.err.println("\n❌ Failed to connect to database!");
            System.err.println("Please check your database credentials in DBConnection.java");
            System.exit(1);
        }

        // Show login menu
        SellerLogin login = new SellerLogin();
        int sellerId = login.showLoginMenu();
        
        if (sellerId != -1) {
            // Show product management menu
            ProductManager productManager = new ProductManager(sellerId);
            productManager.showMenu();
            productManager.close();
        }
        
        login.close();
        DBConnection.closeConnection();
        
        System.out.println("\n👋 Thank you for using E-Commerce Seller Dashboard!");
    }
}