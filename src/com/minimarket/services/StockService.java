package com.minimarket.services;

import com.minimarket.models.StockOpname;
import com.minimarket.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockService {
    private ProductService productService;

    public StockService() {
        this.productService = new ProductService();
    }

    public boolean recordStockOpname(StockOpname opname) {
        String sql = "INSERT INTO stock_opname (product_id, opname_date, system_stock, " +
                "physical_stock, difference, notes, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, opname.getProductId());
            pstmt.setTimestamp(2, Timestamp.valueOf(opname.getOpnameDate()));
            pstmt.setInt(3, opname.getSystemStock());
            pstmt.setInt(4, opname.getPhysicalStock());
            pstmt.setInt(5, opname.getDifference());
            pstmt.setString(6, opname.getNotes());
            pstmt.setInt(7, opname.getUserId());

            // Update product stock if there's a difference
            if (opname.getDifference() != 0) {
                updateProductStock(opname.getProductId(), opname.getDifference());
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateProductStock(int productId, int adjustment) {
        String sql = "UPDATE products SET stock = stock + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adjustment);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StockOpname> getStockOpnameHistory() {
        List<StockOpname> history = new ArrayList<>();
        // Implementation depends on your database schema
        return history;
    }

    public List<com.minimarket.models.Product> getLowStockAlert() {
        return productService.getLowStockProducts();
    }
}