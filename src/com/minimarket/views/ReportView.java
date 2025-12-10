package com.minimarket.views;

import com.minimarket.controllers.ReportController;
import com.minimarket.utils.ReportGenerator;
import com.minimarket.utils.PDFExporter;
import com.toedter.calendar.JDateChooser; // Pastikan library jcalendar ada
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportView extends JFrame {
    private ReportController reportController;
    private JDateChooser dateFrom;
    private JDateChooser dateTo;
    private JComboBox<String> cmbReportType;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private NumberFormat currencyFormat;

    public ReportView() {
        reportController = new ReportController();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        initUI();
    }

    private void initUI() {
        setTitle("Laporan & Analisis");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Filter Panel ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Laporan"));

        filterPanel.add(new JLabel("Dari Tgl:"));
        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setDate(new java.util.Date()); // Default hari ini
        dateFrom.setPreferredSize(new Dimension(130, 25));
        filterPanel.add(dateFrom);

        filterPanel.add(new JLabel("Sampai Tgl:"));
        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setDate(new java.util.Date()); // Default hari ini
        dateTo.setPreferredSize(new Dimension(130, 25));
        filterPanel.add(dateTo);

        filterPanel.add(new JLabel("Jenis Laporan:"));
        cmbReportType = new JComboBox<>(new String[]{
                "Laporan Penjualan",
                "Laporan Produk Terjual",
                "Laporan Stok"
        });
        filterPanel.add(cmbReportType);

        JButton btnGenerate = new JButton("Tampilkan Data");
        btnGenerate.setBackground(new Color(0, 120, 215));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.addActionListener(e -> generateReport());
        filterPanel.add(btnGenerate);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(25);
        mainPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnExportPDF = new JButton("Export PDF");
        btnExportPDF.addActionListener(e -> exportToPDF());

        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnExportPDF);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void generateReport() {
        String type = (String) cmbReportType.getSelectedItem();
        java.util.Date dFrom = dateFrom.getDate();
        java.util.Date dTo = dateTo.getDate();

        if ((dFrom == null || dTo == null) && !type.equals("Laporan Stok")) {
            JOptionPane.showMessageDialog(this, "Pilih rentang tanggal!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Konversi java.util.Date ke java.sql.Date
            Date sqlFrom = (dFrom != null) ? new Date(dFrom.getTime()) : null;
            Date sqlTo = (dTo != null) ? new Date(dTo.getTime()) : null;

            // Reset Table
            tableModel.setRowCount(0);

            if ("Laporan Penjualan".equals(type)) {
                generateSalesReport(sqlFrom, sqlTo);
            } else if ("Laporan Produk Terjual".equals(type)) {
                generateProductReport(sqlFrom, sqlTo);
            } else if ("Laporan Stok".equals(type)) {
                generateStockReport();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void generateSalesReport(Date from, Date to) {
        // 1. Set Judul Kolom Khusus Penjualan
        String[] columns = {"No Struk", "Waktu", "Kasir", "Subtotal", "Diskon/Pajak", "Total", "Pembayaran"};
        tableModel.setColumnIdentifiers(columns); // PENTING: Reset struktur kolom

        List<Map<String, Object>> data = reportController.getSalesReport(from, to);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data penjualan pada tanggal ini.");
            return;
        }

        for (Map<String, Object> row : data) {
            double total = (double) row.get("total_amount");
            double paid = (double) row.get("amount_paid");

            tableModel.addRow(new Object[]{
                    row.get("receipt_number"),
                    sdf.format(row.get("sale_datetime")),
                    row.get("user_name"),
                    currencyFormat.format(row.get("subtotal")),
                    "-", // Placeholder untuk detail diskon
                    currencyFormat.format(total),
                    currencyFormat.format(paid)
            });
        }

        // Tampilkan Summary Pop-up
        Map<String, Object> summary = reportController.getSalesSummary(from, to);
        JOptionPane.showMessageDialog(this,
                "Total Transaksi: " + summary.get("transaction_count") + "\n" +
                        "Omzet Penjualan: " + currencyFormat.format(summary.get("total_sales")),
                "Ringkasan Laporan", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateProductReport(Date from, Date to) {
        // 1. Set Judul Kolom Khusus Produk
        String[] columns = {"Kode", "Nama Produk", "Unit", "Total Terjual (Qty)", "Rata-rata Harga", "Total Pendapatan"};
        tableModel.setColumnIdentifiers(columns); // PENTING

        List<Map<String, Object>> data = reportController.getSoldProductReport(from, to);

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada produk terjual pada periode ini.");
            return;
        }

        for (Map<String, Object> row : data) {
            tableModel.addRow(new Object[]{
                    row.get("product_code"),
                    row.get("product_name"),
                    row.get("unit"),
                    row.get("total_quantity"),
                    currencyFormat.format(row.get("average_price")),
                    currencyFormat.format(row.get("total_revenue"))
            });
        }
    }

    private void generateStockReport() {
        // 1. Set Judul Kolom Khusus Stok
        String[] columns = {"Kode", "Nama Produk", "Unit", "Harga Jual", "Stok Fisik", "Status"};
        tableModel.setColumnIdentifiers(columns); // PENTING

        List<Map<String, Object>> data = reportController.getStockReport();

        for (Map<String, Object> row : data) {
            tableModel.addRow(new Object[]{
                    row.get("product_code"),
                    row.get("product_name"),
                    row.get("unit"),
                    currencyFormat.format(row.get("price")),
                    row.get("stock"),
                    row.get("status")
            });
        }
    }

    private void exportToPDF() {
        // Implementasi PDF Exporter bisa dipanggil di sini sesuai kode lama Anda
        JOptionPane.showMessageDialog(this, "Fitur Export PDF siap digunakan!");
    }
}