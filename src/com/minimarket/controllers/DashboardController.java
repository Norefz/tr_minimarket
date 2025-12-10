package com.minimarket.controllers;

import com.minimarket.services.SaleService;
import com.minimarket.services.ProductService;
import com.minimarket.services.ReportService;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardController {
    private SaleService saleService;
    private ProductService productService;
    private ReportService reportService;

    public DashboardController() {
        this.saleService = new SaleService();
        this.productService = new ProductService();
        this.reportService = new ReportService();
    }

    public double getTodaySales() {
        // Mengambil tanggal hari ini
        Date today = Date.valueOf(LocalDate.now());
        // Memanggil service untuk total uang
        return saleService.getDailyTotal(today);
    }

    public int getTodayTransactions() {
        // Mengambil tanggal hari ini
        Date today = Date.valueOf(LocalDate.now());
        // PERBAIKAN: Memanggil service untuk jumlah transaksi (sebelumnya return 0)
        return saleService.getDailyTransactionCount(today);
    }

    public List<com.minimarket.models.Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

    public Map<String, Object> getSalesSummary(Date startDate, Date endDate) {
        return reportService.getSalesSummary(startDate, endDate);
    }
}