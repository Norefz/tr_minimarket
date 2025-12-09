package com.minimarket.services;

import com.minimarket.models.Sale;
import com.minimarket.models.SaleItem;
import com.minimarket.database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SaleService {
    private ProductService productService;

    public SaleService() {
        this.productService = new ProductService();
    }

    public boolean createSale(Sale sale) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert sale
            String sql = "INSERT INTO sales (store_id, user_id, receipt_number, sale_datetime, " +
                    "subtotal, total_amount, amount_paid, change_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, sale.getStoreId());
            pstmt.setInt(2, sale.getUserId());
            pstmt.setString(3, sale.getReceiptNumber());
            pstmt.setTimestamp(4, Timestamp.valueOf(sale.getSaleDatetime()));
            pstmt.setDouble(5, sale.getSubtotal());
            pstmt.setDouble(6, sale.getTotalAmount());
            pstmt.setDouble(7, sale.getAmountPaid());
            pstmt.setDouble(8, sale.getChangeAmount());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating sale failed, no rows affected.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int saleId = generatedKeys.getInt(1);
                sale.setId(saleId);

                // Insert sale items
                if (!createSaleItems(saleId, sale.getItems(), conn)) {
                    throw new SQLException("Failed to create sale items.");
                }

                // Update product stocks
                for (SaleItem item : sale.getItems()) {
                    if (!productService.updateStock(item.getProductId(), item.getQuantity())) {
                        throw new SQLException("Failed to update stock for product ID: " + item.getProductId());
                    }
                }

                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean createSaleItems(int saleId, List<SaleItem> items, Connection conn) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, product_id, quantity, price, line_total) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (SaleItem item : items) {
                pstmt.setInt(1, saleId);
                pstmt.setInt(2, item.getProductId());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setDouble(4, item.getPrice());
                pstmt.setDouble(5, item.getLineTotal());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public String generateReceiptNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "INV-" + LocalDateTime.now().format(formatter);
    }

    public List<Sale> getSalesByDate(java.sql.Date date) {
        return getSalesByDateRange(date, date);
    }

    public List<Sale> getSalesByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        // This method would query the database
        // Implementation depends on your database schema
        return null;
    }

    public Sale getSaleById(int id) {
        // This method would query the database
        // Implementation depends on your database schema
        return null;
    }

    public double getDailyTotal(java.sql.Date date) {
        // This method would query the database
        // Implementation depends on your database schema
        return 0;
    }
}