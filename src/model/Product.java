package model;

public class Product {
    private int productId;      // Changed from 'id' to match your DB
    private int sellerId;       // Changed from 'sellerId' to match your DB
    private String name;
    private String category;
    private double price;
    private double discount;
    private int stock;
    private String description;

    // Constructors
    public Product() {}

    public Product(int productId, String name, String category, double price, 
                   double discount, int stock, String description, int sellerId) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.stock = stock;
        this.description = description;
        this.sellerId = sellerId;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Calculate final price after discount
    public double getFinalPrice() {
        return price - (price * discount / 100);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", stock=" + stock +
                ", finalPrice=" + getFinalPrice() +
                '}';
    }
}