package com.minimarket.controllers;

import com.minimarket.services.SaleService;
import com.minimarket.models.Sale;
import com.minimarket.models.SaleItem;
import com.minimarket.models.Product;
import java.util.List;

public class SaleController {
    private SaleService saleService;

    public SaleController() {
        this.saleService = new SaleService();
    }

    public Sale createSale(List<SaleItem> items, String paymentMethod,
                           double amountPaid, int customerId,
                           double discount, double tax) {
        // Validate sale
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Keranjang tidak boleh kosong");
        }

        // Create sale object
        Sale sale = new Sale();
        sale.setItems(items);
        sale.setPaymentMethod(paymentMethod);
        sale.setAmountPaid(amountPaid);
        sale.setCustomerId(customerId);
        sale.setDiscount(discount);
        sale.setTax(tax);

        // Set current user
        sale.setUserId(LoginController.getCurrentUser().getId());
        sale.setStoreId(1); // Default store

        // Generate receipt number
        sale.setReceiptNumber(saleService.generateReceiptNumber());

        // Calculate totals
        sale.calculateChange();

        // Save to database
        if (saleService.createSale(sale)) {
            return sale;
        }

        return null;
    }

    public String generateReceiptNumber() {
        return saleService.generateReceiptNumber();
    }

    public List<Sale> getSalesToday() {
        // Implementation depends on your database schema
        return null;
    }

    public Sale getSaleById(int id) {
        return saleService.getSaleById(id);
    }
}