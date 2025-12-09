package com.minimarket.models;

import java.time.LocalDateTime;
import java.util.List;

public class Purchase {
    private int id;
    private String purchaseNumber;
    private int supplierId;
    private LocalDateTime purchaseDate;
    private double totalAmount;
    private String status; // pending, completed, cancelled
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Supplier supplier;
    private List<PurchaseItem> items;

    public Purchase() {
        this.purchaseDate = LocalDateTime.now();
        this.status = "pending";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPurchaseNumber() { return purchaseNumber; }
    public void setPurchaseNumber(String purchaseNumber) { this.purchaseNumber = purchaseNumber; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) {
        this.items = items;
        calculateTotal();
    }

    private void calculateTotal() {
        if (items != null) {
            this.totalAmount = items.stream()
                    .mapToDouble(PurchaseItem::getSubtotal)
                    .sum();
        }
    }
}