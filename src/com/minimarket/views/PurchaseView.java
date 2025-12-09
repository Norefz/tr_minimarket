package com.minimarket.views;

import com.minimarket.controllers.PurchaseController;
import com.minimarket.controllers.ProductController;
import com.minimarket.models.Purchase;
import com.minimarket.models.PurchaseItem;
import com.minimarket.models.Product;
import com.minimarket.services.PurchaseService; // Direct access helper
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PurchaseView extends JFrame {
    private PurchaseController purchaseController;
    private ProductController productController;
    private List<PurchaseItem> purchaseItems;
    private DefaultTableModel tableModel;
    private JTable itemTable;

    // Components
    private JComboBox<String> cmbSupplier; // String dummy dulu
    private JTextField txtProductCode;
    private JTextField txtQuantity;
    private JTextField txtBuyPrice;
    private JLabel lblProductName;
    private JLabel lblTotal;

    private Product currentProduct; // Produk yang sedang dipilih
    private NumberFormat currencyFormat;

    public PurchaseView() {
        purchaseController = new PurchaseController();
        productController = new ProductController();
        purchaseItems = new ArrayList<>();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        initUI();
    }

    private void initUI() {
        setTitle("Transaksi Pembelian (Restock)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP: Supplier Info ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Info Supplier"));

        topPanel.add(new JLabel("Supplier:"));
        // Idealnya load dari database via SupplierController.
        // Disini kita hardcode ID: 1 (Pastikan di DB tabel suppliers ada data ID 1)
        String[] suppliers = {"1 - Supplier Umum", "2 - Supplier Makanan", "3 - Supplier Minuman"};
        cmbSupplier = new JComboBox<>(suppliers);
        topPanel.add(cmbSupplier);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Input & Table ---
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        // Input Product Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Barang Masuk"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Cari Produk
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Kode Produk:"), gbc);

        txtProductCode = new JTextField(15);
        txtProductCode.addActionListener(e -> searchProduct());
        gbc.gridx = 1;
        inputPanel.add(txtProductCode, gbc);

        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(e -> searchProduct());
        gbc.gridx = 2;
        inputPanel.add(btnSearch, gbc);

        lblProductName = new JLabel("-");
        lblProductName.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 3;
        inputPanel.add(lblProductName, gbc);

        // Row 2: Qty & Harga Beli
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Qty Masuk:"), gbc);
        txtQuantity = new JTextField("1", 10);
        gbc.gridx = 1;
        inputPanel.add(txtQuantity, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Harga Beli (Satuan):"), gbc);
        txtBuyPrice = new JTextField("0", 10);
        gbc.gridx = 1;
        inputPanel.add(txtBuyPrice, gbc);

        JButton btnAdd = new JButton("Tambah ke List");
        btnAdd.addActionListener(e -> addItem());
        gbc.gridx = 2; gbc.gridy = 2;
        inputPanel.add(btnAdd, gbc);

        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Kode", "Nama Item", "Harga Beli", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        itemTable = new JTable(tableModel);
        centerPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Actions & Total ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblTotal = new JLabel("Total: Rp 0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("SIMPAN TRANSAKSI");
        btnSave.setBackground(new Color(0, 100, 0));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.addActionListener(e -> saveTransaction());

        JButton btnCancel = new JButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        bottomPanel.add(lblTotal, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void searchProduct() {
        String code = txtProductCode.getText().trim();
        if(code.isEmpty()) return;

        currentProduct = productController.getProductByCode(code);
        if(currentProduct != null) {
            lblProductName.setText(currentProduct.getName());
            txtBuyPrice.setText(String.valueOf(currentProduct.getPrice())); // Default ke harga jual, user bisa ubah
            txtQuantity.requestFocus();
            txtQuantity.selectAll();
        } else {
            lblProductName.setText("Produk tidak ditemukan!");
            currentProduct = null;
        }
    }

    private void addItem() {
        if(currentProduct == null) {
            JOptionPane.showMessageDialog(this, "Cari produk terlebih dahulu!");
            return;
        }

        try {
            int qty = Integer.parseInt(txtQuantity.getText());
            double price = Double.parseDouble(txtBuyPrice.getText());

            if(qty <= 0) throw new NumberFormatException();

            // Cek jika item sudah ada di list
            boolean exists = false;
            for(PurchaseItem item : purchaseItems) {
                if(item.getProductId() == currentProduct.getId()) {
                    item.setQuantity(item.getQuantity() + qty);
                    item.setPrice(price); // Update harga terbaru
                    item.setSubtotal(item.getQuantity() * price);
                    exists = true;
                    break;
                }
            }

            if(!exists) {
                PurchaseItem item = new PurchaseItem();
                item.setProductId(currentProduct.getId());
                item.setProduct(currentProduct);
                item.setQuantity(qty);
                item.setPrice(price);
                item.setSubtotal(qty * price);
                purchaseItems.add(item);
            }

            refreshTable();
            clearInput();

        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Qty dan Harga harus angka valid!");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        double total = 0;

        for(PurchaseItem item : purchaseItems) {
            Object[] row = {
                    item.getProduct().getCode(),
                    item.getProduct().getName(),
                    currencyFormat.format(item.getPrice()),
                    item.getQuantity(),
                    currencyFormat.format(item.getSubtotal())
            };
            tableModel.addRow(row);
            total += item.getSubtotal();
        }

        lblTotal.setText("Total: " + currencyFormat.format(total));
    }

    private void clearInput() {
        txtProductCode.setText("");
        lblProductName.setText("-");
        txtQuantity.setText("1");
        txtBuyPrice.setText("0");
        currentProduct = null;
        txtProductCode.requestFocus();
    }

    private void saveTransaction() {
        if(purchaseItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "List barang kosong!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Simpan transaksi pembelian?\nStok produk akan bertambah.", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                Purchase purchase = new Purchase();

                // Ambil ID Supplier dari ComboBox (Parsing string "1 - Supplier...")
                String supplierStr = (String) cmbSupplier.getSelectedItem();
                int supplierId = Integer.parseInt(supplierStr.split(" - ")[0]);

                purchase.setSupplierId(supplierId);
                purchase.setItems(purchaseItems);
                purchase.setPurchaseNumber(new PurchaseService().generateInvoiceNumber());

                if(purchaseController.addPurchase(purchase)) {
                    JOptionPane.showMessageDialog(this, "Pembelian berhasil disimpan!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan pembelian.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}