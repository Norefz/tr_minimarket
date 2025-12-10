package com.minimarket.controllers;

import com.minimarket.services.SaleService;
import com.minimarket.models.Sale;
import com.minimarket.models.SaleItem;
import com.minimarket.controllers.LoginController; // Untuk ambil user login
import java.util.List;

public class SaleController {
    private SaleService saleService;

    public SaleController() {
        this.saleService = new SaleService();
    }

    // Method ini yang akan dipanggil oleh View
    public Sale createSale(List<SaleItem> items, String paymentMethod,
                           double amountPaid, int customerId,
                           double discount, double tax) {

        // 1. Validasi Input Dasar
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Keranjang belanja kosong!");
        }

        // 2. Buat Objek Sale
        Sale sale = new Sale();
        sale.setItems(items); // Ini akan otomatis hitung subtotal di model Sale

        // 3. Set Data Tambahan
        sale.setPaymentMethod(paymentMethod);
        sale.setAmountPaid(amountPaid);
        sale.setDiscount(discount);
        sale.setTax(tax);
        sale.setCustomerId(customerId); // 0 jika customer umum

        // 4. Set User yang Login & Store (Default 1)
        com.minimarket.models.User currentUser = LoginController.getCurrentUser();
        if (currentUser != null) {
            sale.setUserId(currentUser.getId());
            sale.setUser(currentUser); // Untuk keperluan cetak struk
        } else {
            // Opsional: Throw error jika harus login
            // throw new IllegalStateException("Kasir belum login!");
            sale.setUserId(1); // Fallback ke admin jika testing
        }

        sale.setStoreId(1); // Default Store ID dari database kamu

        // 5. Generate Nomor Struk
        sale.setReceiptNumber(saleService.generateReceiptNumber());

        // 6. Hitung Kembalian & Total Akhir (Penting!)
        // Logic ini ada di Model Sale (calculateTotals & calculateChange),
        // tapi kita panggil ulang/pastikan ter-set.
        // *Karena Model Sale.setItems() memanggil calculateTotals(),
        // kita perlu set discount & tax SEBELUM setItems atau panggil calculate manual.
        // Mari kita hitung manual di sini untuk memastikan urutan benar:

        double subtotal = items.stream().mapToDouble(SaleItem::getLineTotal).sum();
        sale.setSubtotal(subtotal);

        double afterDiscount = subtotal - discount;
        double taxAmount = afterDiscount * (tax / 100);
        double totalAmount = afterDiscount + taxAmount;

        sale.setTotalAmount(totalAmount);
        sale.setChangeAmount(amountPaid - totalAmount); // Hitung kembalian

        // 7. Simpan ke Database via Service
        if (saleService.createSale(sale)) {
            return sale; // Kembalikan objek sale yang sudah punya ID dari DB
        }

        return null; // Gagal simpan
    }
}