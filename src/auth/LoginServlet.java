package auth;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import db.DBConnection;
import com.google.gson.Gson;

// REMOVED @WebServlet annotation - using web.xml mapping instead
public class LoginServlet extends HttpServlet {
    private Gson gson = new Gson();

    // Login
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        System.out.println("=== LoginServlet doPost called ===");
        System.out.println("PathInfo: " + pathInfo);
        System.out.println("RequestURI: " + request.getRequestURI());
        System.out.println("ContextPath: " + request.getContextPath());
        System.out.println("ServletPath: " + request.getServletPath());
        
        if (pathInfo != null && pathInfo.equals("/login")) {
            handleLogin(request, response);
        } else if (pathInfo != null && pathInfo.equals("/logout")) {
            handleLogout(request, response);
        } else if (pathInfo != null && pathInfo.equals("/register")) {
            handleRegister(request, response);
        } else {
            System.out.println("ERROR: Endpoint not found - pathInfo: " + pathInfo);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Endpoint not found\"}");
        }
    }

    // Check session
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        System.out.println("=== LoginServlet doGet called ===");
        System.out.println("PathInfo: " + request.getPathInfo());
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sellerId") != null) {
            String username = (String) session.getAttribute("username");
            int sellerId = (int) session.getAttribute("sellerId");
            
            String json = String.format("{\"authenticated\":true,\"username\":\"%s\",\"sellerId\":%d}", 
                                       username, sellerId);
            response.getWriter().write(json);
        } else {
            response.getWriter().write("{\"authenticated\":false}");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        System.out.println("=== handleLogin called ===");
        try {
            BufferedReader reader = request.getReader();
            LoginRequest loginReq = gson.fromJson(reader, LoginRequest.class);
            
            System.out.println("Login attempt for username: " + loginReq.username);
            
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM sellers WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loginReq.username);
            stmt.setString(2, loginReq.password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("sellerId", rs.getInt("seller_id"));
                session.setAttribute("username", rs.getString("username"));
                
                System.out.println("Login successful for: " + rs.getString("username"));
                
                String json = String.format("{\"success\":true,\"username\":\"%s\",\"sellerId\":%d}", 
                                           rs.getString("username"), rs.getInt("seller_id"));
                response.getWriter().write(json);
            } else {
                System.out.println("Login failed - invalid credentials");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\":false,\"error\":\"Invalid credentials\"}");
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        System.out.println("=== handleLogout called ===");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.getWriter().write("{\"success\":true,\"message\":\"Logged out successfully\"}");
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        System.out.println("=== handleRegister called ===");
        try {
            BufferedReader reader = request.getReader();
            RegisterRequest regReq = gson.fromJson(reader, RegisterRequest.class);
            
            System.out.println("Registration attempt for username: " + regReq.username);
            
            Connection conn = DBConnection.getConnection();
            
            // Check if username exists
            String checkSql = "SELECT seller_id FROM sellers WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, regReq.username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Registration failed - username already exists");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"success\":false,\"error\":\"Username already exists\"}");
                rs.close();
                checkStmt.close();
                return;
            }
            rs.close();
            checkStmt.close();
            
            // Insert new seller
            String sql = "INSERT INTO sellers (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, regReq.username);
            stmt.setString(2, regReq.password);
            
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            
            if (keys.next()) {
                int sellerId = keys.getInt(1);
                HttpSession session = request.getSession(true);
                session.setAttribute("sellerId", sellerId);
                session.setAttribute("username", regReq.username);
                
                System.out.println("Registration successful for: " + regReq.username);
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                String json = String.format("{\"success\":true,\"username\":\"%s\",\"sellerId\":%d}", 
                                           regReq.username, sellerId);
                response.getWriter().write(json);
            }
            
            keys.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Helper classes
    private static class LoginRequest {
        String username;
        String password;
    }

    private static class RegisterRequest {
        String username;
        String password;
        String email;
    }
}