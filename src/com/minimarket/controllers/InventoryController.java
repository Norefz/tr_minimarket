package com.minimarket.controllers;

import com.minimarket.services.InventoryService;
import com.minimarket.models.Stock;
import java.util.List;

public class InventoryController {
    private InventoryService inventoryService;

    public InventoryController() {
        this.inventoryService = new InventoryService();
    }

    public List<Stock> getAllStocks() {
        // Pastikan data sinkron dulu sebelum diambil
        inventoryService.syncStockTable();
        return inventoryService.getAllStocks();
    }

    public boolean updateStockLevels(int stockId, int min, int max) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Nilai tidak boleh negatif!");
        }
        if (min > max) {
            throw new IllegalArgumentException("Min stok tidak boleh lebih besar dari Max stok!");
        }
        return inventoryService.updateStockLevels(stockId, min, max);
    }
}