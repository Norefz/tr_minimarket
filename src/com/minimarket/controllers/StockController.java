package com.minimarket.controllers;

import com.minimarket.services.StockService;
import com.minimarket.models.StockOpname;
import com.minimarket.models.Product;
import java.util.List;

public class StockController {
    private StockService stockService;

    public StockController() {
        this.stockService = new StockService();
    }

    public boolean recordStockOpname(StockOpname opname) {
        // 1. Validasi Produk
        if (opname.getProductId() <= 0) {
            throw new IllegalArgumentException("Produk harus dipilih terlebih dahulu!");
        }

        // 2. Validasi Stok Fisik
        // Stok fisik boleh 0 (habis), tapi tidak boleh negatif
        if (opname.getPhysicalStock() < 0) {
            throw new IllegalArgumentException("Stok fisik tidak boleh bernilai negatif!");
        }

        // 3. Validasi User
        // Memastikan ada user yang bertanggung jawab (login)
        if (opname.getUserId() <= 0) {
            // Kita bisa melempar error atau membiarkannya (nanti ditangani di Service menjadi NULL/Admin Default)
            // throw new IllegalArgumentException("Sesi login tidak valid. Silakan relogin.");
        }

        // Kirim ke Service untuk disimpan
        return stockService.recordStockOpname(opname);
    }

    public List<StockOpname> getStockOpnameHistory() {
        return stockService.getStockOpnameHistory();
    }

    public List<Product> getLowStockAlert() {
        return stockService.getLowStockAlert();
    }
}