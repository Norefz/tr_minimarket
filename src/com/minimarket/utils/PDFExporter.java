package com.minimarket.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PDFExporter {

    public static void exportSalesReportToPDF(List<Map<String, Object>> reportData,
                                              String filePath,
                                              Date startDate, Date endDate) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("LAPORAN PENJUALAN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Store info
        Font storeFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph storeInfo = new Paragraph("MINIMARKET BERKAH\nJl. Gunung Payung 1", storeFont);
        storeInfo.setAlignment(Element.ALIGN_CENTER);
        storeInfo.setSpacingAfter(10);
        document.add(storeInfo);

        // Date range
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Paragraph dateRange = new Paragraph(
                "Periode: " + sdf.format(startDate) + " - " + sdf.format(endDate),
                FontFactory.getFont(FontFactory.HELVETICA, 12)
        );
        dateRange.setAlignment(Element.ALIGN_CENTER);
        dateRange.setSpacingAfter(20);
        document.add(dateRange);

        // Create table
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        // Table headers
        String[] headers = {"No. Struk", "Tanggal", "Kasir", "Subtotal", "Total", "Bayar", "Kembali"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Table data
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        double totalSales = 0;

        for (Map<String, Object> row : reportData) {
            table.addCell(new Phrase((String) row.get("receipt_number")));
            table.addCell(new Phrase(dateFormat.format(row.get("sale_datetime"))));
            table.addCell(new Phrase((String) row.get("user_name")));
            table.addCell(new Phrase(formatCurrency((double) row.get("subtotal"))));
            table.addCell(new Phrase(formatCurrency((double) row.get("total_amount"))));
            table.addCell(new Phrase(formatCurrency((double) row.get("amount_paid"))));
            table.addCell(new Phrase(formatCurrency((double) row.get("change_amount"))));

            totalSales += (double) row.get("total_amount");
        }

        document.add(table);

        // Summary
        Paragraph summary = new Paragraph(
                String.format("\n\nTotal Penjualan: Rp %,.0f", totalSales),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)
        );
        summary.setSpacingBefore(20);
        document.add(summary);

        // Footer
        Paragraph footer = new Paragraph(
                "\n\nDicetak pada: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)
        );
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        document.close();
    }

    public static void exportProductReportToPDF(List<Map<String, Object>> reportData,
                                                String filePath,
                                                Date startDate, Date endDate) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("LAPORAN PRODUK TERJUAL", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Paragraph dateRange = new Paragraph(
                "Periode: " + sdf.format(startDate) + " - " + sdf.format(endDate),
                FontFactory.getFont(FontFactory.HELVETICA, 12)
        );
        dateRange.setAlignment(Element.ALIGN_CENTER);
        dateRange.setSpacingAfter(20);
        document.add(dateRange);

        // Create table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        // Table headers
        String[] headers = {"Kode Produk", "Nama Produk", "Unit", "Total Qty", "Rata Harga", "Total Revenue"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Table data
        double totalRevenue = 0;
        int totalQty = 0;

        for (Map<String, Object> row : reportData) {
            table.addCell(new Phrase((String) row.get("product_code")));
            table.addCell(new Phrase((String) row.get("product_name")));
            table.addCell(new Phrase((String) row.get("unit")));
            table.addCell(new Phrase(String.valueOf(row.get("total_quantity"))));
            table.addCell(new Phrase(formatCurrency((double) row.get("average_price"))));
            table.addCell(new Phrase(formatCurrency((double) row.get("total_revenue"))));

            totalRevenue += (double) row.get("total_revenue");
            totalQty += (int) row.get("total_quantity");
        }

        document.add(table);

        // Summary
        Paragraph summary = new Paragraph(
                String.format("\n\nTotal Produk Terjual: %d pcs\nTotal Pendapatan: Rp %,.0f",
                        totalQty, totalRevenue),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)
        );
        summary.setSpacingBefore(20);
        document.add(summary);

        document.close();
    }

    public static void exportStockReportToPDF(List<Map<String, Object>> reportData,
                                              String filePath) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("LAPORAN STOK PRODUK", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        Paragraph date = new Paragraph(
                "Periode: " + new SimpleDateFormat("dd MMMM yyyy").format(new Date()),
                FontFactory.getFont(FontFactory.HELVETICA, 12)
        );
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20);
        document.add(date);

        // Create table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        // Table headers
        String[] headers = {"Kode", "Nama Produk", "Unit", "Harga", "Stok", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Table data
        int lowStockCount = 0;
        int totalStock = 0;
        double totalValue = 0;

        for (Map<String, Object> row : reportData) {
            String status = (String) row.get("status");
            int stock = (int) row.get("stock");
            double price = (double) row.get("price");

            PdfPCell cell1 = new PdfPCell(new Phrase((String) row.get("product_code")));
            PdfPCell cell2 = new PdfPCell(new Phrase((String) row.get("product_name")));
            PdfPCell cell3 = new PdfPCell(new Phrase((String) row.get("unit")));
            PdfPCell cell4 = new PdfPCell(new Phrase(formatCurrency(price)));
            PdfPCell cell5 = new PdfPCell(new Phrase(String.valueOf(stock)));
            PdfPCell cell6 = new PdfPCell(new Phrase(status));

            // Color coding for status
            if ("HABIS".equals(status)) {
                cell6.setBackgroundColor(BaseColor.RED);
            } else if ("RENDAH".equals(status)) {
                cell6.setBackgroundColor(BaseColor.ORANGE);
            }

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);

            if ("HABIS".equals(status) || "RENDAH".equals(status)) {
                lowStockCount++;
            }

            totalStock += stock;
            totalValue += stock * price;
        }

        document.add(table);

        // Summary
        Paragraph summary = new Paragraph(
                String.format("\n\nTotal Produk: %d\n" +
                                "Produk Stok Rendah/Habis: %d\n" +
                                "Total Stok: %d pcs\n" +
                                "Total Nilai Stok: Rp %,.0f",
                        reportData.size(), lowStockCount, totalStock, totalValue),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)
        );
        summary.setSpacingBefore(20);
        document.add(summary);

        document.close();
    }

    private static String formatCurrency(double amount) {
        return String.format("Rp %,.0f", amount);
    }
}