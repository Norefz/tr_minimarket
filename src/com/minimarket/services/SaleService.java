package com.minimarket.services;

import com.minimarket.models.Sale;
import com.minimarket.models.SaleItem;
import com.minimarket.database.DatabaseConnection;
import java.sql.*;
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
            conn.setAutoCommit(false); // Mulai Transaksi

            // PERBAIKAN:
            // 1. Menambahkan payment_method, customer_id, discount, tax
            // 2. Mengubah change_amount menjadi charge_amount (sesuai database kamu)
            String sql = "INSERT INTO sales (store_id, user_id, receipt_number, sale_datetime, " +
                    "subtotal, total_amount, amount_paid, charge_amount, " +
                    "payment_method, customer_id, discount, tax) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, sale.getStoreId());

            // Cek user id (handle jika null/logout)
            if(sale.getUserId() > 0) pstmt.setInt(2, sale.getUserId());
            else pstmt.setNull(2, Types.INTEGER);

            pstmt.setString(3, sale.getReceiptNumber());
            pstmt.setTimestamp(4, Timestamp.valueOf(sale.getSaleDatetime()));
            pstmt.setDouble(5, sale.getSubtotal());
            pstmt.setDouble(6, sale.getTotalAmount());
            pstmt.setDouble(7, sale.getAmountPaid());
            pstmt.setDouble(8, sale.getChangeAmount()); // Mapping ke charge_amount DB
            pstmt.setString(9, sale.getPaymentMethod());

            // Handle Customer ID (bisa null jika pelanggan umum)
            if (sale.getCustomerId() > 0) {
                pstmt.setInt(10, sale.getCustomerId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.setDouble(11, sale.getDiscount());
            pstmt.setDouble(12, sale.getTax());

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
                    // PERBAIKAN DI SINI:
                    // Kirim 'conn' ke dalam method updateStock
                    boolean success = productService.updateStock(item.getProductId(), item.getQuantity(), conn);

                    if (!success) {
                        throw new SQLException("Gagal update stok untuk produk ID: " + item.getProductId() + ". Stok mungkin tidak cukup.");
                    }
                }

                conn.commit(); // Sekarang ini akan berhasil karena koneksi belum ditutup
                return true;

            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Batalkan jika error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
                // conn.close() ditangani oleh DatabaseConnection atau biarkan terbuka jika singleton
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
                if (result == Statement.EXECUTE_FAILED) {
                    return false;
                }
            }
            return true;
        }
    }

    // ... method lain (generateReceiptNumber dll) biarkan tetap sama ...
    public String generateReceiptNumber() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "INV-" + java.time.LocalDateTime.now().format(formatter);
    }

    public com.minimarket.models.Sale getSaleById(int id) { return null; }
    public double getDailyTotal(java.sql.Date date) { return 0; }
}