package com.minimarket.utils;

import com.minimarket.models.Sale;
import com.minimarket.models.SaleItem;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    public static String generateReceipt(Sale sale) {
        StringBuilder receipt = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,###");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        receipt.append("========================================\n");
        receipt.append(centerText("MINIMARKET BERKAH", 40)).append("\n");
        receipt.append(centerText("Jl. GUNUNG PAYUNG 1", 40)).append("\n");
        receipt.append(centerText("Telp: 081247168869", 40)).append("\n");
        receipt.append("========================================\n");

        // PERBAIKAN DI SINI: Gunakan dtf.format() untuk LocalDateTime
        receipt.append("No. Struk : ").append(sale.getReceiptNumber()).append("\n");
        receipt.append("Tanggal   : ").append(dtf.format(sale.getSaleDatetime())).append("\n");
        receipt.append("Kasir     : ").append(sale.getUser() != null ? sale.getUser().getName() : "-").append("\n");
        receipt.append("----------------------------------------\n");

        // Items header
        receipt.append(String.format("%-20s %5s %10s\n", "Barang", "Qty", "Total"));
        receipt.append("----------------------------------------\n");

        // Items
        for (SaleItem item : sale.getItems()) {
            String name = item.getProduct().getName();
            if (name.length() > 20) {
                name = name.substring(0, 17) + "...";
            }

            String qty = String.valueOf(item.getQuantity());
            String total = "Rp" + df.format(item.getLineTotal());

            receipt.append(String.format("%-20s %5s %10s\n", name, qty, total));

            // Price per unit
            receipt.append(String.format("  @Rp%s\n", df.format(item.getPrice())));
        }

        receipt.append("----------------------------------------\n");

        // PERBAIKAN: Hitung ulang taxAmount
        double taxAmount = (sale.getSubtotal() - sale.getDiscount()) * (sale.getTax() / 100);

        // Payment summary
        receipt.append(String.format("%-27s %11s\n", "Subtotal:", "Rp" + df.format(sale.getSubtotal())));

        if (sale.getDiscount() > 0) {
            receipt.append(String.format("%-27s %11s\n", "Diskon:", "-Rp" + df.format(sale.getDiscount())));
        }

        if (taxAmount > 0) {
            receipt.append(String.format("%-27s %11s\n", "Pajak (" + sale.getTax() + "%):", "Rp" + df.format(taxAmount)));
        }

        receipt.append(String.format("%-27s %11s\n", "TOTAL:", "Rp" + df.format(sale.getTotalAmount())));
        receipt.append(String.format("%-27s %11s\n", "Bayar:", "Rp" + df.format(sale.getAmountPaid())));
        receipt.append(String.format("%-27s %11s\n", "Kembali:", "Rp" + df.format(sale.getChangeAmount())));

        receipt.append("----------------------------------------\n");
        receipt.append("Pembayaran: ").append(sale.getPaymentMethod()).append("\n");

        if (sale.getCustomer() != null && sale.getCustomer().getName() != null && !sale.getCustomer().getName().isEmpty()) {
            receipt.append("Customer : ").append(sale.getCustomer().getName()).append("\n");
        }

        receipt.append("\n");
        receipt.append(centerText("Terima kasih telah berbelanja!", 40)).append("\n");
        receipt.append("========================================\n");

        return receipt.toString();
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }

        int padding = (width - text.length()) / 2;
        StringBuilder centered = new StringBuilder();

        for (int i = 0; i < padding; i++) {
            centered.append(" ");
        }

        centered.append(text);

        while (centered.length() < width) {
            centered.append(" ");
        }

        return centered.toString();
    }
}