package com.minimarket.services;

import com.minimarket.models.Stock;
import com.minimarket.models.Product;
import com.minimarket.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {

    // Mengambil semua data stok lengkap dengan info produk
    public List<Stock> getAllStocks() {
        List<Stock> stocks = new ArrayList<>();
        // Query Join untuk mengambil nama produk juga
        String sql = "SELECT s.*, p.code, p.name, p.unit, p.stock as real_stock " +
                "FROM stocks s " +
                "JOIN products p ON s.product_id = p.id " +
                "ORDER BY p.name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Stock stock = new Stock();
                stock.setId(rs.getInt("id"));
                stock.setProductId(rs.getInt("product_id"));
                // Kita ambil quantity dari tabel products agar selalu sinkron
                stock.setQuantity(rs.getInt("real_stock"));
                stock.setMinStock(rs.getInt("min_stock"));
                stock.setMaxStock(rs.getInt("max_stock"));
                stock.setLastUpdate(rs.getTimestamp("last_update").toLocalDateTime());

                // Set object Product di dalam Stock
                Product p = new Product();
                p.setId(rs.getInt("product_id"));
                p.setCode(rs.getString("code"));
                p.setName(rs.getString("name"));
                p.setUnit(rs.getString("unit"));
                stock.setProduct(p);

                stocks.add(stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    // Update Batas Minimum dan Maksimum
    public boolean updateStockLevels(int stockId, int min, int max) {
        String sql = "UPDATE stocks SET min_stock = ?, max_stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, min);
            pstmt.setInt(2, max);
            pstmt.setInt(3, stockId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Sinkronisasi otomatis (dipanggil saat aplikasi start atau refresh)
    public void syncStockTable() {
        // Query ini memastikan tabel stocks selalu punya data untuk setiap product
        String sql = "INSERT INTO stocks (product_id, quantity) " +
                "SELECT id, stock FROM products " +
                "WHERE id NOT IN (SELECT product_id FROM stocks)";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}