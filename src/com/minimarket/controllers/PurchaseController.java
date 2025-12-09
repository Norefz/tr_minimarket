package com.minimarket.controllers;

import com.minimarket.services.PurchaseService;
import com.minimarket.models.Purchase;
import java.util.List;

public class PurchaseController {
    private PurchaseService purchaseService;

    public PurchaseController() {
        this.purchaseService = new PurchaseService();
    }

    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    public boolean addPurchase(Purchase purchase) {
        // Validate
        if (purchase.getSupplierId() <= 0) {
            throw new IllegalArgumentException("Supplier harus dipilih");
        }
        if (purchase.getItems() == null || purchase.getItems().isEmpty()) {
            throw new IllegalArgumentException("Item pembelian tidak boleh kosong");
        }

        return purchaseService.addPurchase(purchase);
    }

    public boolean updatePurchaseStatus(int purchaseId, String status) {
        return purchaseService.updatePurchaseStatus(purchaseId, status);
    }
}