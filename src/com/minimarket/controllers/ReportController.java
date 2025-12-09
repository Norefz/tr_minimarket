package com.minimarket.controllers;

import com.minimarket.services.ReportService;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class ReportController {
    private ReportService reportService;

    public ReportController() {
        this.reportService = new ReportService();
    }

    public List<Map<String, Object>> getSalesReport(Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        return reportService.getSalesReport(startDate, endDate);
    }

    public List<Map<String, Object>> getSoldProductReport(Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        return reportService.getSoldProductReport(startDate, endDate);
    }

    public List<Map<String, Object>> getStockReport() {
        return reportService.getStockReport();
    }

    public Map<String, Object> getSalesSummary(Date startDate, Date endDate) {
        validateDateRange(startDate, endDate);
        return reportService.getSalesSummary(startDate, endDate);
    }

    private void validateDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Tanggal harus diisi");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Tanggal mulai harus sebelum tanggal akhir");
        }
    }
}