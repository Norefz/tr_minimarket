package com.minimarket.utils;

import com.minimarket.models.Sale;
import com.minimarket.models.SaleItem;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ReceiptGenerator {

    public static String generate58mmReceipt(Sale sale) {
        StringBuilder receipt = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,###");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Store header
        receipt.append("========================================\n");
        receipt.append(centerText("MINIMARKET SUKA MAJU", 40)).append("\n");
        receipt.append(centerText("Jl. Contoh No. 123", 40)).append("\n");
        receipt.append(centerText("Telp: 021-12345678", 40)).append("\n");
        receipt.append("========================================\n");

        // Sale information
        receipt.append("No. Struk : ").append(sale.getReceiptNumber()).append("\n");
        receipt.append("Tanggal   : ").append(sdf.format(sale.getSaleDatetime())).append("\n");
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

        // Payment summary
        receipt.append(String.format("%-27s %11s\n", "Subtotal:", "Rp" + df.format(sale.getSubtotal())));

        if (sale.getDiscount() > 0) {
            receipt.append(String.format("%-27s %11s\n", "Diskon:", "-Rp" + df.format(sale.getDiscount())));
        }

        double taxAmount = (sale.getSubtotal() - sale.getDiscount()) * (sale.getTax() / 100);
        if (taxAmount > 0) {
            receipt.append(String.format("%-27s %11s\n", "Pajak (" + sale.getTax() + "%):", "Rp" + df.format(taxAmount)));
        }

        receipt.append(String.format("%-27s %11s\n", "TOTAL:", "Rp" + df.format(sale.getTotalAmount())));
        receipt.append(String.format("%-27s %11s\n", "Bayar:", "Rp" + df.format(sale.getAmountPaid())));
        receipt.append(String.format("%-27s %11s\n", "Kembali:", "Rp" + df.format(sale.getChangeAmount())));

        receipt.append("----------------------------------------\n");
        receipt.append("Pembayaran: ").append(sale.getPaymentMethod()).append("\n");

        if (sale.getCustomer() != null && !sale.getCustomer().getName().isEmpty()) {
            receipt.append("Customer : ").append(sale.getCustomer().getName()).append("\n");
        }

        if (sale.getMemberPoints() > 0) {
            receipt.append("Points   : +").append(sale.getMemberPoints()).append(" pts\n");
        }

        if ("QRIS".equalsIgnoreCase(sale.getPaymentMethod())) {
            receipt.append("\n[QRIS PAYMENT]\n");
            receipt.append(centerText("SCAN QR CODE DI BAWAH", 40)).append("\n");
            receipt.append(centerText("[QR CODE IMAGE]", 40)).append("\n");
        }

        receipt.append("\n");
        receipt.append(centerText("Terima kasih telah berbelanja!", 40)).append("\n");
        receipt.append(centerText("Barang yang sudah dibeli", 40)).append("\n");
        receipt.append(centerText("tidak dapat ditukar/dikembalikan", 40)).append("\n");
        receipt.append("========================================\n");

        return receipt.toString();
    }

    public static String generateReceipt(Sale sale, boolean forPrinting) {
        if (forPrinting) {
            return generate58mmReceipt(sale);
        } else {
            // Generate a more detailed receipt for display
            return generateDetailedReceipt(sale);
        }
    }

    private static String generateDetailedReceipt(Sale sale) {
        StringBuilder receipt = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,###");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        receipt.append("========================================\n");
        receipt.append("             STRUK PENJUALAN            \n");
        receipt.append("========================================\n");
        receipt.append("No. Struk    : ").append(sale.getReceiptNumber()).append("\n");
        receipt.append("Tanggal      : ").append(sdf.format(sale.getSaleDatetime())).append("\n");
        receipt.append("Kasir        : ").append(sale.getUser() != null ? sale.getUser().getName() : "-").append("\n");

        if (sale.getCustomer() != null) {
            receipt.append("Customer     : ").append(sale.getCustomer().getName()).append("\n");
        }

        receipt.append("========================================\n");
        receipt.append("            DAFTAR BARANG               \n");
        receipt.append("========================================\n");

        for (SaleItem item : sale.getItems()) {
            receipt.append(String.format("%-20s %5s x Rp%9s\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    df.format(item.getPrice())));
            receipt.append(String.format("                    = Rp%9s\n", df.format(item.getLineTotal())));
        }

        receipt.append("========================================\n");
        receipt.append(String.format("%-20s Rp%13s\n", "Subtotal:", df.format(sale.getSubtotal())));
        receipt.append(String.format("%-20s Rp%13s\n", "Diskon:", df.format(sale.getDiscount())));

        double taxAmount = (sale.getSubtotal() - sale.getDiscount()) * (sale.getTax() / 100);
        receipt.append(String.format("%-20s Rp%13s\n", "Pajak (" + sale.getTax() + "%):", df.format(taxAmount)));
        receipt.append(String.format("%-20s Rp%13s\n", "Total:", df.format(sale.getTotalAmount())));
        receipt.append(String.format("%-20s Rp%13s\n", "Bayar:", df.format(sale.getAmountPaid())));
        receipt.append(String.format("%-20s Rp%13s\n", "Kembali:", df.format(sale.getChangeAmount())));
        receipt.append("========================================\n");
        receipt.append("Pembayaran   : ").append(sale.getPaymentMethod()).append("\n");

        if (sale.getMemberPoints() > 0) {
            receipt.append("Points Earned: +").append(sale.getMemberPoints()).append(" pts\n");
        }

        receipt.append("========================================\n");
        receipt.append("Terima kasih telah berbelanja!\n");
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