package com.minimarket.models;

import java.time.LocalDateTime;

public class Stock {
    private int id;
    private int productId;
    private int quantity;
    private LocalDateTime lastUpdate;
    private int minStock;
    private int maxStock;

    private Product product;

    public Stock() {
        this.lastUpdate = LocalDateTime.now();
        this.minStock = 10;
        this.maxStock = 100;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.lastUpdate = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }

    public int getMaxStock() { return maxStock; }
    public void setMaxStock(int maxStock) { this.maxStock = maxStock; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}