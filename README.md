📋 Project Overview
A full-stack web application that allows sellers to manage their product inventory through a secure, web-based dashboard. The system provides authentication, CRUD operations for products, and real-time inventory tracking.
________________________________________

Request Flow Example (Add Product)
1.	User Action: User fills product form and clicks "Save Product"
2.	Frontend: JavaScript sends POST request to /ecommerce-seller/api/products/
3.	Tomcat: Routes request to ProductServlet.doPost()
4.	Authentication: Servlet checks session for sellerId
5.	Processing: Servlet parses JSON, creates Product object
6.	Database: JDBC inserts product into MySQL products table
7.	Response: Servlet returns JSON with created product
8.	Frontend: Updates UI with new product, refreshes table
________________________________________
🛠️ Technologies Used

Backend
Technology	Version	Purpose
Java	JDK 8+	Core programming language

Servlets	4.0	HTTP request handling

JDBC	MySQL Connector 8.0.33	Database connectivity

Gson	2.10.1	JSON serialization/deserialization

Apache Tomcat	9.0.113	Web application server

MySQL	8.0+	Relational database

Frontend
Technology	Purpose

HTML5	Structure

CSS3	Styling (Gradients, Flexbox, Grid)

JavaScript (ES6+)	Client-side logic, Fetch API

Build & Deployment

Tool	Purpose

Apache Ant	Build automation

WAR packaging	Deployment format


Development Tools

•	VS Code - IDE

•	MySQL Workbench - Database management

•	Postman - API testing

•	Browser DevTools - Frontend debugging


🔐 Key Features Implemented
1. Authentication System
•	 User registration with duplicate username prevention
•	 Secure login with credential validation
•	 Session-based authentication (HttpSession)
•	 Protected routes (auth check on all API endpoints)
•	 Logout functionality with session invalidation
2. Product Management (CRUD)
•	 Create: Add new products with validation
•	 Read: View all products, search/filter
•	 Update: Edit product details
•	 Delete: Remove products with confirmation
3. Dashboard Features
•	 Real-time statistics (total products, stock levels)
•	 Dynamic product table with status badges
•	 Modal-based forms for add/edit
•	 Inventory status indicators (In Stock, Low Stock, Out of Stock)
4. Security Features
•	 Session-based authentication
•	 CORS configuration for secure cross-origin requests
•	 Seller isolation (sellers can only see their own products)
•	 Input validation on both client and server side



<img width="450" height="470" alt="image" src="https://github.com/user-attachments/assets/6637898a-5f67-4302-ac6d-6a0225f55aa4" />
<img width="450" height="428" alt="image" src="https://github.com/user-attachments/assets/5872c176-209f-4f48-89a0-f2014a48db14" />
<img width="450" height="566" alt="image" src="https://github.com/user-attachments/assets/b5a716aa-f2e2-4365-9f61-6d2d4d2e609f" />
<img width="450" height="650" alt="image" src="https://github.com/user-attachments/assets/d3217099-c89f-44b0-94a6-d172eab061ec" />

  
