package com.minimarket.utils;

import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportGenerator {

    public static void printReport(TableModel model, String reportTitle) {
        try {
            StringBuilder report = new StringBuilder();

            // Report header
            report.append("========================================\n");
            report.append(centerText(reportTitle, 40)).append("\n");
            report.append("Tanggal: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
            report.append("========================================\n\n");

            // Column headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                report.append(String.format("%-20s", model.getColumnName(i)));
            }
            report.append("\n");
            report.append("-".repeat(20 * model.getColumnCount())).append("\n");

            // Data rows
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    String cellValue = (value != null) ? value.toString() : "";
                    report.append(String.format("%-20s", truncate(cellValue, 18)));
                }
                report.append("\n");
            }

            report.append("\n========================================\n");
            report.append("Total Records: ").append(model.getRowCount()).append("\n");
            report.append("========================================\n");

            // Print or save to file
            System.out.println(report.toString());

            // Optionally save to file
            String filename = reportTitle.toLowerCase().replace(" ", "_") + "_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(report.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating report: " + e.getMessage());
        }
    }

    public static void exportToExcel(TableModel model, String reportTitle) {
        try {
            StringBuilder csv = new StringBuilder();

            // Column headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.append("\"").append(model.getColumnName(i)).append("\"");
                if (i < model.getColumnCount() - 1) {
                    csv.append(",");
                }
            }
            csv.append("\n");

            // Data rows
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    String cellValue = (value != null) ? value.toString().replace("\"", "\"\"") : "";
                    csv.append("\"").append(cellValue).append("\"");
                    if (col < model.getColumnCount() - 1) {
                        csv.append(",");
                    }
                }
                csv.append("\n");
            }

            // Save to CSV file
            String filename = reportTitle.toLowerCase().replace(" ", "_") + "_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(csv.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error exporting to Excel: " + e.getMessage());
        }
    }

    public static String generateSalesSummaryReport(double totalSales, int transactionCount,
                                                    Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder report = new StringBuilder();

        report.append("========================================\n");
        report.append(centerText("LAPORAN SUMMARY PENJUALAN", 40)).append("\n");
        report.append("Periode: ").append(sdf.format(startDate)).append(" - ").append(sdf.format(endDate)).append("\n");
        report.append("Tanggal: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        report.append("========================================\n\n");

        report.append(String.format("%-30s: %15s\n", "Total Transaksi", transactionCount));
        report.append(String.format("%-30s: Rp %12,.0f\n", "Total Penjualan", totalSales));
        report.append(String.format("%-30s: Rp %12,.0f\n", "Rata-rata per Transaksi",
                transactionCount > 0 ? totalSales / transactionCount : 0));

        report.append("\n========================================\n");

        return report.toString();
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

    private static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}