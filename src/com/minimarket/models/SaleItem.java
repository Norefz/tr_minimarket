package com.minimarket.models;

import java.time.LocalDateTime;

public class SaleItem {
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private double price;
    private double lineTotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Product product;

    public SaleItem() {}

    public SaleItem(int productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.lineTotal = quantity * price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
        calculateLineTotal();
    }

    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    private void calculateLineTotal() {
        this.lineTotal = this.quantity * this.price;
    }
}