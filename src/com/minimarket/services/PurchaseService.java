package com.minimarket.services;

import com.minimarket.models.Purchase;
import com.minimarket.models.PurchaseItem;
import com.minimarket.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseService {

    public List<Purchase> getAllPurchases() {
        List<Purchase> purchases = new ArrayList<>();
        // Implementasi read data jika diperlukan nanti
        return purchases;
    }

    public boolean addPurchase(Purchase purchase) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Mulai Transaksi

            // 1. Insert Header Pembelian
            String sql = "INSERT INTO purchases (invoice_number, purchase_date, supplier_id, total_amount, payment_method, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, purchase.getPurchaseNumber());
            pstmt.setTimestamp(2, Timestamp.valueOf(purchase.getPurchaseDate()));
            pstmt.setInt(3, purchase.getSupplierId());
            pstmt.setDouble(4, purchase.getTotalAmount());
            pstmt.setString(5, "CASH"); // Default atau ambil dari input
            pstmt.setString(6, "Restock Barang"); // Default notes

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating purchase failed, no rows affected.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int purchaseId = generatedKeys.getInt(1);
                purchase.setId(purchaseId);

                // 2. Insert Item & Update Stok
                if (!createPurchaseItems(purchaseId, purchase.getItems(), conn)) {
                    throw new SQLException("Failed to create purchase items.");
                }

                conn.commit(); // Simpan Permanen
                return true;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Batalkan jika error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
                // Jangan close connection di sini jika singleton, tapi kalau mau aman close stmt saja
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean createPurchaseItems(int purchaseId, List<PurchaseItem> items, Connection conn) throws SQLException {
        String sqlItem = "INSERT INTO purchase_items (purchase_id, product_id, quantity, price, line_total) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE products SET stock = stock + ?, price = ? WHERE id = ?"; // Update stok & harga beli terbaru

        try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem);
             PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {

            for (PurchaseItem item : items) {
                // Insert Item
                pstmtItem.setInt(1, purchaseId);
                pstmtItem.setInt(2, item.getProductId());
                pstmtItem.setInt(3, item.getQuantity());
                pstmtItem.setDouble(4, item.getPrice());
                pstmtItem.setDouble(5, item.getSubtotal());
                pstmtItem.addBatch();

                // Update Stock Product (Menambah Stok)
                pstmtStock.setInt(1, item.getQuantity());
                pstmtStock.setDouble(2, item.getPrice()); // Update harga dasar produk sesuai harga beli terbaru (opsional)
                pstmtStock.setInt(3, item.getProductId());
                pstmtStock.addBatch();
            }

            pstmtItem.executeBatch();
            pstmtStock.executeBatch();
            return true;
        }
    }

    public boolean updatePurchaseStatus(int purchaseId, String status) {
        return false; // Belum diimplementasikan
    }

    // Helper untuk generate nomor faktur
    public String generateInvoiceNumber() {
        return "INV-IN-" + System.currentTimeMillis();
    }
}