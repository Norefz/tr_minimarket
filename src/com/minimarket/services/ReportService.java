package com.minimarket.services;

import com.minimarket.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    // 1. Laporan Penjualan (Detail Transaksi)
    public List<Map<String, Object>> getSalesReport(java.sql.Date startDate, java.sql.Date endDate) {
        List<Map<String, Object>> report = new ArrayList<>();

        // PERBAIKAN: Ganti 'change_amount' menjadi 'charge_amount' sesuai database Anda
        // ATAU cek struktur database Anda, jika namanya 'charge_amount', pakai itu.
        String sql = "SELECT s.receipt_number, s.sale_datetime, u.name as user_name, " +
                "s.subtotal, s.total_amount, s.amount_paid, s.charge_amount " + // <-- Ganti s.change_amount jadi s.charge_amount
                "FROM sales s " +
                "LEFT JOIN users u ON s.user_id = u.id " +
                "WHERE DATE(s.sale_datetime) BETWEEN ? AND ? " +
                "ORDER BY s.sale_datetime DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("receipt_number", rs.getString("receipt_number"));
                row.put("sale_datetime", rs.getTimestamp("sale_datetime"));
                row.put("user_name", rs.getString("user_name") != null ? rs.getString("user_name") : "-");
                row.put("subtotal", rs.getDouble("subtotal"));
                row.put("total_amount", rs.getDouble("total_amount"));
                row.put("amount_paid", rs.getDouble("amount_paid"));

                // PERBAIKAN: Ambil dari 'charge_amount'
                row.put("change_amount", rs.getDouble("charge_amount"));

                report.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    // ... (Sisa kode method lain seperti getSoldProductReport, getStockReport, getSalesSummary tetap sama) ...
    // Pastikan untuk menyalin sisa method dari file sebelumnya agar tidak hilang.

    // 2. Laporan Produk Terjual (Best Seller)
    public List<Map<String, Object>> getSoldProductReport(java.sql.Date startDate, java.sql.Date endDate) {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT p.code, p.name, p.unit, " +
                "SUM(si.quantity) as total_qty, " +
                "AVG(si.price) as avg_price, " +
                "SUM(si.line_total) as total_revenue " +
                "FROM sale_items si " +
                "JOIN products p ON si.product_id = p.id " +
                "JOIN sales s ON si.sale_id = s.id " +
                "WHERE DATE(s.sale_datetime) BETWEEN ? AND ? " +
                "GROUP BY p.id, p.code, p.name, p.unit " +
                "ORDER BY total_qty DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("product_code", rs.getString("code"));
                row.put("product_name", rs.getString("name"));
                row.put("unit", rs.getString("unit"));
                row.put("total_quantity", rs.getInt("total_qty"));
                row.put("average_price", rs.getDouble("avg_price"));
                row.put("total_revenue", rs.getDouble("total_revenue"));
                report.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    // 3. Laporan Stok (Inventory)
    public List<Map<String, Object>> getStockReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT code, name, unit, price, stock FROM products ORDER BY stock ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("product_code", rs.getString("code"));
                row.put("product_name", rs.getString("name"));
                row.put("unit", rs.getString("unit"));
                row.put("price", rs.getDouble("price"));
                row.put("stock", rs.getInt("stock"));

                int stock = rs.getInt("stock");
                String status = "AMAN";
                if (stock == 0) status = "HABIS";
                else if (stock <= 10) status = "KRITIS";

                row.put("status", status);
                report.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    // 4. Ringkasan
    public Map<String, Object> getSalesSummary(java.sql.Date startDate, java.sql.Date endDate) {
        Map<String, Object> summary = new HashMap<>();
        String sql = "SELECT COUNT(*) as transaction_count, SUM(total_amount) as total_sales " +
                "FROM sales WHERE DATE(sale_datetime) BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                summary.put("transaction_count", rs.getInt("transaction_count"));
                double total = rs.getObject("total_sales") != null ? rs.getDouble("total_sales") : 0.0;
                summary.put("total_sales", total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }
}