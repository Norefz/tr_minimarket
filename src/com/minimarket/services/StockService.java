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
        // PERBAIKAN: Menggunakan nama kolom yang sesuai dengan database (old_stock, new_stock)
        String sql = "INSERT INTO stock_opname (product_id,  old_stock, " +
                "new_stock, difference, notes, opname_date, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, opname.getProductId());
            pstmt.setTimestamp(2, Timestamp.valueOf(opname.getOpnameDate()));

            // Mapping: systemStock (Java) -> old_stock (Database)
            pstmt.setInt(3, opname.getSystemStock());

            // Mapping: physicalStock (Java) -> new_stock (Database)
            pstmt.setInt(4, opname.getPhysicalStock());

            pstmt.setInt(5, opname.getDifference());
            pstmt.setString(6, opname.getNotes());

            // Cek jika user login valid, jika tidak set null/0 (untuk mencegah error foreign key jika user dihapus)
            if (opname.getUserId() > 0) {
                pstmt.setInt(7, opname.getUserId());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }

            // Update stok master produk jika ada selisih
            if (opname.getDifference() != 0) {
                updateProductStock(opname.getProductId(), opname.getDifference());
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Cek console untuk detail error jika masih gagal
        }
        return false;
    }

    private void updateProductStock(int productId, int adjustment) {
        // Query untuk update stok langsung di tabel products
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
        // Implementasi untuk mengambil history bisa ditambahkan di sini nanti
        return history;
    }

    public List<com.minimarket.models.Product> getLowStockAlert() {
        return productService.getLowStockProducts();
    }
}