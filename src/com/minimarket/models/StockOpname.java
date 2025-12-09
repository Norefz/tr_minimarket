package com.minimarket.models;

import java.time.LocalDateTime;

public class StockOpname {
    private int id;
    private int productId;
    private LocalDateTime opnameDate;
    private int systemStock;
    private int physicalStock;
    private int difference;
    private String notes;
    private int userId;

    private Product product;
    private User user;

    public StockOpname() {
        this.opnameDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public LocalDateTime getOpnameDate() { return opnameDate; }
    public void setOpnameDate(LocalDateTime opnameDate) { this.opnameDate = opnameDate; }

    public int getSystemStock() { return systemStock; }
    public void setSystemStock(int systemStock) { this.systemStock = systemStock; }

    public int getPhysicalStock() { return physicalStock; }
    public void setPhysicalStock(int physicalStock) { this.physicalStock = physicalStock; }

    public int getDifference() {
        return physicalStock - systemStock;
    }

    public void setDifference(int difference) { this.difference = difference; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}