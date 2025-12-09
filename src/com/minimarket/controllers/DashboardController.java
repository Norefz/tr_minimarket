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
        Date today = Date.valueOf(LocalDate.now());
        return saleService.getDailyTotal(today);
    }

    public int getTodayTransactions() {
        // Implementation depends on your database schema
        return 0;
    }

    public List<com.minimarket.models.Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

    public Map<String, Object> getSalesSummary(Date startDate, Date endDate) {
        return reportService.getSalesSummary(startDate, endDate);
    }
}