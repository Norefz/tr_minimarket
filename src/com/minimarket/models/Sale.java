package com.minimarket.models;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private int id;
    private int storeId;
    private int userId;
    private String receiptNumber;
    private LocalDateTime saleDatetime;
    private double subtotal;
    private double totalAmount;
    private double amountPaid;
    private double changeAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Extended fields
    private String paymentMethod; // cash, qris, credit
    private int customerId;
    private int memberPoints;
    private double discount;
    private double tax;

    // Relationships
    private Store store;
    private User user;
    private Customer customer;
    private List<SaleItem> items;

    public Sale() {
        this.saleDatetime = LocalDateTime.now();
        this.paymentMethod = "CASH";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public LocalDateTime getSaleDatetime() { return saleDatetime; }
    public void setSaleDatetime(LocalDateTime saleDatetime) { this.saleDatetime = saleDatetime; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getMemberPoints() { return memberPoints; }
    public void setMemberPoints(int memberPoints) { this.memberPoints = memberPoints; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) {
        this.items = items;
        calculateTotals();
    }

    private void calculateTotals() {
        if (items != null) {
            this.subtotal = items.stream()
                    .mapToDouble(SaleItem::getLineTotal)
                    .sum();

            double afterDiscount = subtotal - discount;
            double taxAmount = afterDiscount * (tax / 100);
            this.totalAmount = afterDiscount + taxAmount;
        }
    }

    public void calculateChange() {
        this.changeAmount = amountPaid - totalAmount;
    }
}