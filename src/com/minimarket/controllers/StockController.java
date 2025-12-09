package com.minimarket.controllers;

import com.minimarket.services.StockService;
import com.minimarket.models.StockOpname;
import java.util.List;

public class StockController {
    private StockService stockService;

    public StockController() {
        this.stockService = new StockService();
    }

    public boolean recordStockOpname(StockOpname opname) {
        // Validate
        if (opname.getProductId() <= 0) {
            throw new IllegalArgumentException("Produk harus dipilih");
        }
        if (opname.getPhysicalStock() < 0) {
            throw new IllegalArgumentException("Stok fisik tidak boleh negatif");
        }

        return stockService.recordStockOpname(opname);
    }

    public List<StockOpname> getStockOpnameHistory() {
        return stockService.getStockOpnameHistory();
    }

    public List<com.minimarket.models.Product> getLowStockAlert() {
        return stockService.getLowStockAlert();
    }
}