package product;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import db.DBConnection;
import model.Product;
import com.google.gson.Gson;

// REMOVED @WebServlet annotation - using web.xml mapping instead
public class ProductServlet extends HttpServlet {
    private Gson gson = new Gson();

    // GET - Fetch all products or single product
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("=== ProductServlet doGet called ===");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sellerId") == null) {
            System.out.println("ERROR: Not authenticated");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Not authenticated\"}");
            return;
        }

        int sellerId = (int) session.getAttribute("sellerId");
        String pathInfo = request.getPathInfo();
        
        System.out.println("PathInfo: " + pathInfo);
        System.out.println("SellerId: " + sellerId);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all products
                System.out.println("Fetching all products for seller: " + sellerId);
                List<Product> products = getAllProducts(sellerId);
                System.out.println("Found " + products.size() + " products");
                response.getWriter().write(gson.toJson(products));
            } else {
                // Get single product
                int productId = Integer.parseInt(pathInfo.substring(1));
                System.out.println("Fetching product ID: " + productId);
                Product product = getProduct(productId, sellerId);
                if (product != null) {
                    response.getWriter().write(gson.toJson(product));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Product not found\"}");
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR in doGet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST - Add new product
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("=== ProductServlet doPost called ===");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sellerId") == null) {
            System.out.println("ERROR: Not authenticated");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Not authenticated\"}");
            return;
        }

        int sellerId = (int) session.getAttribute("sellerId");
        System.out.println("Adding product for seller: " + sellerId);
        
        try {
            BufferedReader reader = request.getReader();
            Product product = gson.fromJson(reader, Product.class);
            product.setSellerId(sellerId);
            
            System.out.println("Product data: " + product.getName());
            
            int productId = addProduct(product);
            product.setProductId(productId);
            
            System.out.println("Product added successfully with ID: " + productId);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(product));
        } catch (Exception e) {
            System.err.println("ERROR adding product: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // PUT - Update product
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("=== ProductServlet doPut called ===");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sellerId") == null) {
            System.out.println("ERROR: Not authenticated");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Not authenticated\"}");
            return;
        }

        int sellerId = (int) session.getAttribute("sellerId");
        String pathInfo = request.getPathInfo();
        
        System.out.println("PathInfo: " + pathInfo);
        System.out.println("SellerId: " + sellerId);
        
        try {
            int productId = Integer.parseInt(pathInfo.substring(1));
            System.out.println("Updating product ID: " + productId);
            
            BufferedReader reader = request.getReader();
            Product product = gson.fromJson(reader, Product.class);
            product.setProductId(productId);
            product.setSellerId(sellerId);
            
            boolean updated = updateProduct(product);
            if (updated) {
                System.out.println("Product updated successfully");
                response.getWriter().write(gson.toJson(product));
            } else {
                System.out.println("ERROR: Product not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Product not found\"}");
            }
        } catch (Exception e) {
            System.err.println("ERROR updating product: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // DELETE - Delete product
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("=== ProductServlet doDelete called ===");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sellerId") == null) {
            System.out.println("ERROR: Not authenticated");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Not authenticated\"}");
            return;
        }

        int sellerId = (int) session.getAttribute("sellerId");
        String pathInfo = request.getPathInfo();
        
        System.out.println("PathInfo: " + pathInfo);
        System.out.println("SellerId: " + sellerId);
        
        try {
            int productId = Integer.parseInt(pathInfo.substring(1));
            System.out.println("Deleting product ID: " + productId);
            
            boolean deleted = deleteProduct(productId, sellerId);
            
            if (deleted) {
                System.out.println("Product deleted successfully");
                response.getWriter().write("{\"message\":\"Product deleted successfully\"}");
            } else {
                System.out.println("ERROR: Product not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Product not found\"}");
            }
        } catch (Exception e) {
            System.err.println("ERROR deleting product: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Database Methods
    private List<Product> getAllProducts(int sellerId) throws SQLException {
        List<Product> products = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM products WHERE seller_id = ? ORDER BY product_id DESC";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, sellerId);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            products.add(extractProduct(rs));
        }
        
        rs.close();
        stmt.close();
        return products;
    }

    private Product getProduct(int productId, int sellerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM products WHERE product_id = ? AND seller_id = ?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, productId);
        stmt.setInt(2, sellerId);
        ResultSet rs = stmt.executeQuery();
        
        Product product = null;
        if (rs.next()) {
            product = extractProduct(rs);
        }
        
        rs.close();
        stmt.close();
        return product;
    }

    private int addProduct(Product product) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO products (name, category, price, discount, stock, description, seller_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, product.getName());
        stmt.setString(2, product.getCategory());
        stmt.setDouble(3, product.getPrice());
        stmt.setDouble(4, product.getDiscount());
        stmt.setInt(5, product.getStock());
        stmt.setString(6, product.getDescription());
        stmt.setInt(7, product.getSellerId());
        
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        int id = 0;
        if (rs.next()) {
            id = rs.getInt(1);
        }
        
        rs.close();
        stmt.close();
        return id;
    }

    private boolean updateProduct(Product product) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE products SET name=?, category=?, price=?, discount=?, " +
                     "stock=?, description=? WHERE product_id=? AND seller_id=?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, product.getName());
        stmt.setString(2, product.getCategory());
        stmt.setDouble(3, product.getPrice());
        stmt.setDouble(4, product.getDiscount());
        stmt.setInt(5, product.getStock());
        stmt.setString(6, product.getDescription());
        stmt.setInt(7, product.getProductId());
        stmt.setInt(8, product.getSellerId());
        
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows > 0;
    }

    private boolean deleteProduct(int productId, int sellerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM products WHERE product_id = ? AND seller_id = ?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, productId);
        stmt.setInt(2, sellerId);
        
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows > 0;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getDouble("price"),
            rs.getDouble("discount"),
            rs.getInt("stock"),
            rs.getString("description"),
            rs.getInt("seller_id")
        );
    }
}