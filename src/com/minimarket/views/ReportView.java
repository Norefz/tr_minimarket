package com.minimarket.views;

import com.minimarket.controllers.ReportController;
import com.minimarket.utils.ReportGenerator;
import com.minimarket.utils.PDFExporter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JCalendar;


public class ReportView extends JFrame {
    private ReportController reportController;
    private JDateChooser dateFrom;
    private JDateChooser dateTo;
    private JComboBox<String> cmbReportType;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ReportView() {
        reportController = new ReportController();
        initUI();
    }

    private void initUI() {
        setTitle("Laporan");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Laporan"));

        filterPanel.add(new JLabel("Dari:"));
        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setDate(new java.util.Date());
        dateFrom.setPreferredSize(new Dimension(120, 25));
        filterPanel.add(dateFrom);

        filterPanel.add(new JLabel("Sampai:"));
        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setDate(new java.util.Date());
        dateTo.setPreferredSize(new Dimension(120, 25));
        filterPanel.add(dateTo);

        filterPanel.add(new JLabel("Jenis Laporan:"));
        cmbReportType = new JComboBox<>(new String[]{
                "Laporan Penjualan",
                "Laporan Produk Terjual",
                "Laporan Stok"
        });
        filterPanel.add(cmbReportType);

        JButton btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(e -> generateReport());
        filterPanel.add(btnGenerate);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Preview Laporan"));

        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnPrint = new JButton("Print");
        JButton btnExportPDF = new JButton("Export PDF");
        JButton btnExportExcel = new JButton("Export Excel");
        JButton btnClose = new JButton("Tutup");

        btnPrint.addActionListener(e -> printReport());
        btnExportPDF.addActionListener(e -> exportToPDF());
        btnExportExcel.addActionListener(e -> exportToExcel());
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnExportPDF);
        buttonPanel.add(btnExportExcel);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void generateReport() {
        java.util.Date from = dateFrom.getDate();
        java.util.Date to = dateTo.getDate();
        String reportType = (String) cmbReportType.getSelectedItem();

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this,
                    "Pilih tanggal terlebih dahulu!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (from.after(to)) {
            JOptionPane.showMessageDialog(this,
                    "Tanggal 'Dari' harus sebelum tanggal 'Sampai'!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date startDate = new Date(from.getTime());
            Date endDate = new Date(to.getTime());

            if ("Laporan Penjualan".equals(reportType)) {
                generateSalesReport(startDate, endDate);
            } else if ("Laporan Produk Terjual".equals(reportType)) {
                generateSoldProductsReport(startDate, endDate);
            } else if ("Laporan Stok".equals(reportType)) {
                generateStockReport();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generateSalesReport(Date startDate, Date endDate) {
        try {
            List<Map<String, Object>> reportData = reportController.getSalesReport(startDate, endDate);

            String[] columns = {"No. Struk", "Tanggal", "Kasir", "Subtotal", "Total", "Bayar", "Kembali"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Map<String, Object> rowData : reportData) {
                Object[] row = {
                        rowData.get("receipt_number"),
                        sdf.format(rowData.get("sale_datetime")),
                        rowData.get("user_name"),
                        rowData.get("subtotal"),
                        rowData.get("total_amount"),
                        rowData.get("amount_paid"),
                        rowData.get("change_amount")
                };
                tableModel.addRow(row);
            }

            // Show summary
            Map<String, Object> summary = reportController.getSalesSummary(startDate, endDate);
            double totalSales = (double) summary.get("total_sales");
            int transactionCount = (int) summary.get("transaction_count");

            JOptionPane.showMessageDialog(this,
                    String.format("Laporan Penjualan\n" +
                                    "Periode: %s - %s\n\n" +
                                    "Total Transaksi: %d\n" +
                                    "Total Penjualan: Rp %,.2f",
                            new SimpleDateFormat("dd/MM/yyyy").format(startDate),
                            new SimpleDateFormat("dd/MM/yyyy").format(endDate),
                            transactionCount, totalSales),
                    "Summary", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating sales report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateSoldProductsReport(Date startDate, Date endDate) {
        try {
            List<Map<String, Object>> reportData = reportController.getSoldProductReport(startDate, endDate);

            String[] columns = {"Kode Produk", "Nama Produk", "Unit", "Total Qty", "Rata Harga", "Total Revenue"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            for (Map<String, Object> rowData : reportData) {
                Object[] row = {
                        rowData.get("product_code"),
                        rowData.get("product_name"),
                        rowData.get("unit"),
                        rowData.get("total_quantity"),
                        rowData.get("average_price"),
                        rowData.get("total_revenue")
                };
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating sold products report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateStockReport() {
        try {
            List<Map<String, Object>> reportData = reportController.getStockReport();

            String[] columns = {"Kode", "Nama", "Unit", "Harga", "Stok", "Status"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            int lowStockCount = 0;
            int totalStock = 0;
            double totalValue = 0;

            for (Map<String, Object> rowData : reportData) {
                String status = (String) rowData.get("status");
                int stock = (int) rowData.get("stock");
                double price = (double) rowData.get("price");

                if ("HABIS".equals(status) || "RENDAH".equals(status)) {
                    lowStockCount++;
                }

                totalStock += stock;
                totalValue += stock * price;

                Object[] row = {
                        rowData.get("product_code"),
                        rowData.get("product_name"),
                        rowData.get("unit"),
                        price,
                        stock,
                        status
                };
                tableModel.addRow(row);
            }

            JOptionPane.showMessageDialog(this,
                    String.format("Laporan Stok\n\n" +
                                    "Total Produk: %d\n" +
                                    "Produk Stok Rendah/Habis: %d\n" +
                                    "Total Stok: %d pcs\n" +
                                    "Total Nilai Stok: Rp %,.0f",
                            reportData.size(), lowStockCount, totalStock, totalValue),
                    "Stock Summary", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating stock report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReport() {
        try {
            ReportGenerator.printReport(tableModel, (String) cmbReportType.getSelectedItem());
            JOptionPane.showMessageDialog(this,
                    "Report berhasil diprint!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error printing report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToPDF() {
        java.util.Date from = dateFrom.getDate();
        java.util.Date to = dateTo.getDate();
        String reportType = (String) cmbReportType.getSelectedItem();

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this,
                    "Pilih tanggal terlebih dahulu!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date startDate = new Date(from.getTime());
            Date endDate = new Date(to.getTime());

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(
                    reportType.toLowerCase().replace(" ", "_") + "_" +
                            new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".pdf"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                if ("Laporan Penjualan".equals(reportType)) {
                    List<Map<String, Object>> reportData = reportController.getSalesReport(startDate, endDate);
                    PDFExporter.exportSalesReportToPDF(reportData, filePath, startDate, endDate);
                } else if ("Laporan Produk Terjual".equals(reportType)) {
                    List<Map<String, Object>> reportData = reportController.getSoldProductReport(startDate, endDate);
                    PDFExporter.exportProductReportToPDF(reportData, filePath, startDate, endDate);
                } else if ("Laporan Stok".equals(reportType)) {
                    List<Map<String, Object>> reportData = reportController.getStockReport();
                    PDFExporter.exportStockReportToPDF(reportData, filePath);
                }

                JOptionPane.showMessageDialog(this,
                        "Laporan berhasil diexport ke PDF!\n" +
                                "File: " + filePath,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error exporting to PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exportToExcel() {
        try {
            ReportGenerator.exportToExcel(tableModel, (String) cmbReportType.getSelectedItem());
            JOptionPane.showMessageDialog(this,
                    "Report berhasil diexport ke Excel!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error exporting to Excel: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}