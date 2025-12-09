package com.minimarket.views;

import com.minimarket.controllers.LoginController;
import com.minimarket.controllers.DashboardController;
import com.minimarket.models.Product;
import com.minimarket.models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardView extends JFrame {
    private DashboardController dashboardController;
    private JLabel lblWelcome;
    private JLabel lblTodaySales;
    private JLabel lblTodayTransactions;
    private JTable lowStockTable;

    public DashboardView() {
        dashboardController = new DashboardController();
        initUI();
        loadDashboardData();
    }

    private void initUI() {
        setTitle("Dashboard - Minimarket POS");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initMenuBar();
        initContent();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Transaction menu
        JMenu transactionMenu = new JMenu("Transaksi");
        JMenuItem saleItem = new JMenuItem("Penjualan");
        saleItem.addActionListener(e -> openSaleView());
        JMenuItem purchaseItem = new JMenuItem("Pembelian");
        purchaseItem.addActionListener(e -> openPurchaseView());
        transactionMenu.add(saleItem);
        transactionMenu.add(purchaseItem);

        // Master Data menu
        JMenu masterMenu = new JMenu("Master Data");
        JMenuItem productItem = new JMenuItem("Produk");
        productItem.addActionListener(e -> openProductView());
        JMenuItem customerItem = new JMenuItem("Customer");
        customerItem.addActionListener(e -> openCustomerView()); // Ini yang diperbaiki
        JMenuItem supplierItem = new JMenuItem("Supplier");
        supplierItem.addActionListener(e -> openSupplierView());
        JMenuItem userItem = new JMenuItem("User");
        userItem.addActionListener(e -> openUserView());

        masterMenu.add(productItem);
        masterMenu.add(customerItem);
        masterMenu.add(supplierItem);

        // Add user management only for admin
        User currentUser = LoginController.getCurrentUser();
        if (currentUser != null && currentUser.isAdmin()) {
            masterMenu.add(userItem);
        }

        // Report menu
        JMenu reportMenu = new JMenu("Laporan");
        JMenuItem salesReportItem = new JMenuItem("Laporan Penjualan");
        salesReportItem.addActionListener(e -> openSalesReport());
        JMenuItem stockReportItem = new JMenuItem("Laporan Stok");
        stockReportItem.addActionListener(e -> openStockReport());
        JMenuItem productReportItem = new JMenuItem("Laporan Produk Terjual");
        productReportItem.addActionListener(e -> openProductReport());
        reportMenu.add(salesReportItem);
        reportMenu.add(stockReportItem);
        reportMenu.add(productReportItem);

        // Stock menu
        JMenu stockMenu = new JMenu("Stok");
        JMenuItem stockOpnameItem = new JMenuItem("Stock Opname");
        stockOpnameItem.addActionListener(e -> openStockOpname());
        stockMenu.add(stockOpnameItem);

        //Manajemen stok
        JMenuItem inventoryItem = new JMenuItem("Manajemen Batas Stok");
        inventoryItem.addActionListener(e -> {
            InventoryView view = new InventoryView();
            view.setVisible(true);
        });


        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(transactionMenu);
        menuBar.add(masterMenu);
        menuBar.add(stockMenu);
        menuBar.add(reportMenu);
        menuBar.add(helpMenu);
        stockMenu.add(inventoryItem);
        setJMenuBar(menuBar);
    }

    // PERBAIKAN: Method openCustomerView yang benar
    private void openCustomerView() {
        try {
            CustomerView customerView = new CustomerView();
            customerView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Customer View: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openSaleView() {
        try {
            SaleView saleView = new SaleView();
            saleView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Sale View: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openProductView() {
        try {
            ProductView productView = new ProductView();
            productView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Product View: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method lainnya tetap sama...
    private void openPurchaseView() {
        PurchaseView purchaseView = new PurchaseView();
        purchaseView.setVisible(true);
    }

    private void openSupplierView() {
        SupplierView supplierView = new SupplierView();
        supplierView.setVisible(true);
    }

    private void openUserView() {
        UserView userView = new UserView();
        userView.setVisible(true);
    }

    private void openSalesReport() {
        ReportView reportView = new ReportView();
        reportView.setVisible(true);
    }

    private void openStockReport() {
        ReportView reportView = new ReportView();
        reportView.setVisible(true);
    }

    private void openProductReport() {
        ReportView reportView = new ReportView();
        reportView.setVisible(true);
    }

    private void openStockOpname() {
        StockView stockView = new StockView();
        stockView.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            com.minimarket.controllers.LoginController loginController = new com.minimarket.controllers.LoginController();
            loginController.logout();
            this.dispose();
            new LoginView().setVisible(true);
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Minimarket POS System v1.0\n" +
                        "© 2024 Minimarket Solution\n\n" +
                        "Fitur:\n" +
                        "- Authentication & Role Management\n" +
                        "- Point of Sale (POS)\n" +
                        "- Product Management\n" +
                        "- Customer Management\n" +
                        "- Sales Reporting\n" +
                        "- Stock Management",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Method initContent() dan loadDashboardData() tetap seperti sebelumnya...
    private void initContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createTitledBorder("Selamat Datang"));

        User currentUser = LoginController.getCurrentUser();
        String welcomeText = currentUser != null ?
                "Halo, " + currentUser.getName() + " (" + currentUser.getRole() + ")" :
                "Halo, User";

        lblWelcome = new JLabel(welcomeText);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        welcomePanel.add(lblWelcome, BorderLayout.WEST);

        JLabel lblDateTime = new JLabel(new java.util.Date().toString());
        lblDateTime.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomePanel.add(lblDateTime, BorderLayout.EAST);

        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistik Hari Ini"));

        // Today sales
        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        salesPanel.add(new JLabel("Total Penjualan:"), BorderLayout.NORTH);
        lblTodaySales = new JLabel("Rp 0", SwingConstants.CENTER);
        lblTodaySales.setFont(new Font("Arial", Font.BOLD, 24));
        lblTodaySales.setForeground(Color.BLUE);
        salesPanel.add(lblTodaySales, BorderLayout.CENTER);
        statsPanel.add(salesPanel);

        // Today transactions
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        transactionPanel.add(new JLabel("Total Transaksi:"), BorderLayout.NORTH);
        lblTodayTransactions = new JLabel("0", SwingConstants.CENTER);
        lblTodayTransactions.setFont(new Font("Arial", Font.BOLD, 24));
        lblTodayTransactions.setForeground(Color.GREEN);
        transactionPanel.add(lblTodayTransactions, BorderLayout.CENTER);
        statsPanel.add(transactionPanel);

        mainPanel.add(statsPanel, BorderLayout.CENTER);

        // Low stock panel
        JPanel lowStockPanel = new JPanel(new BorderLayout());
        lowStockPanel.setBorder(BorderFactory.createTitledBorder("Produk Stok Rendah"));

        String[] columns = {"Kode", "Nama", "Stok", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        lowStockTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(lowStockTable);
        lowStockPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(lowStockPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadDashboardData() {
        try {
            // Load today's sales
            double todaySales = dashboardController.getTodaySales();
            lblTodaySales.setText(String.format("Rp %,.0f", todaySales));

            // Load today's transactions
            int todayTransactions = dashboardController.getTodayTransactions();
            lblTodayTransactions.setText(String.valueOf(todayTransactions));

            // Load low stock products
            List<Product> lowStockProducts = dashboardController.getLowStockProducts();
            DefaultTableModel model = (DefaultTableModel) lowStockTable.getModel();
            model.setRowCount(0);

            for (Product product : lowStockProducts) {
                String status = product.getStock() == 0 ? "HABIS" : "RENDAH";
                Object[] row = {
                        product.getCode(),
                        product.getName(),
                        product.getStock(),
                        status
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}