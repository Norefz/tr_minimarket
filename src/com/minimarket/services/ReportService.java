package com.minimarket.services;

import com.minimarket.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    public List<Map<String, Object>> getSalesReport(java.sql.Date startDate, java.sql.Date endDate) {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT s.*, u.name as user_name " +
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
                row.put("user_name", rs.getString("user_name"));
                row.put("subtotal", rs.getDouble("subtotal"));
                row.put("total_amount", rs.getDouble("total_amount"));
                row.put("amount_paid", rs.getDouble("amount_paid"));
                row.put("change_amount", rs.getDouble("change_amount"));
                report.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    public List<Map<String, Object>> getSoldProductReport(java.sql.Date startDate, java.sql.Date endDate) {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT si.product_id, p.code, p.name, p.unit, " +
                "SUM(si.quantity) as total_qty, AVG(si.price) as avg_price, " +
                "SUM(si.line_total) as total_revenue " +
                "FROM sale_items si " +
                "JOIN products p ON si.product_id = p.id " +
                "JOIN sales s ON si.sale_id = s.id " +
                "WHERE DATE(s.sale_datetime) BETWEEN ? AND ? " +
                "GROUP BY si.product_id, p.code, p.name, p.unit " +
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

    public List<Map<String, Object>> getStockReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT p.id, p.code, p.name, p.unit, p.price, p.stock, " +
                "CASE WHEN p.stock = 0 THEN 'HABIS' " +
                "WHEN p.stock <= 10 THEN 'RENDAH' " +
                "ELSE 'NORMAL' END as status " +
                "FROM products p " +
                "ORDER BY p.stock ASC";

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
                row.put("status", rs.getString("status"));
                report.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    public Map<String, Object> getSalesSummary(java.sql.Date startDate, java.sql.Date endDate) {
        Map<String, Object> summary = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as transaction_count, " +
                "SUM(total_amount) as total_sales, " +
                "AVG(total_amount) as avg_transaction " +
                "FROM sales " +
                "WHERE DATE(sale_datetime) BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                summary.put("transaction_count", rs.getInt("transaction_count"));
                summary.put("total_sales", rs.getDouble("total_sales"));
                summary.put("avg_transaction", rs.getDouble("avg_transaction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }
}