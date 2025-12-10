package com.minimarket.views;

import com.minimarket.controllers.SaleController;
import com.minimarket.controllers.ProductController;
import com.minimarket.models.Sale;
import com.minimarket.models.SaleItem;
import com.minimarket.models.Product;
import com.minimarket.utils.ReceiptGenerator;
import com.minimarket.services.PrintService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SaleView extends JFrame {
    private SaleController saleController;
    private ProductController productController;
    private List<SaleItem> cartItems;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private JTextField txtBarcode;
    private JTextField txtQuantity;
    private JLabel lblSubtotal;
    private JLabel lblDiscount;
    private JLabel lblTax;
    private JLabel lblTotal;
    private JComboBox<String> cmbPayment;
    private JTextField txtAmountPaid;
    private JTextField txtCustomerName;
    private JTextField txtDiscount;
    private JTextField txtTax;
    private NumberFormat currencyFormat;

    public SaleView() {
        saleController = new SaleController();
        productController = new ProductController();
        cartItems = new ArrayList<>();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        initUI();
    }

    private void initUI() {
        setTitle("Point of Sale (POS)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel - Product input
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Produk"));

        inputPanel.add(new JLabel("Barcode/Kode:"));
        txtBarcode = new JTextField(15);
        inputPanel.add(txtBarcode);

        inputPanel.add(new JLabel("Qty:"));
        txtQuantity = new JTextField(5);
        txtQuantity.setText("1");
        inputPanel.add(txtQuantity);

        JButton btnAdd = new JButton("Tambah");
        btnAdd.addActionListener(e -> addToCart());
        inputPanel.add(btnAdd);

        JButton btnClearInput = new JButton("Clear");
        btnClearInput.addActionListener(e -> clearInput());
        inputPanel.add(btnClearInput);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Center panel - Cart table
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Keranjang Belanja"));

        String[] columns = {"Kode", "Nama", "Harga", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 4) return Double.class;
                if (columnIndex == 3) return Integer.class;
                return String.class;
            }
        };

        cartTable = new JTable(cartTableModel);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        // Cart buttons
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRemove = new JButton("Hapus Item");
        JButton btnClearCart = new JButton("Kosongkan Keranjang");

        btnRemove.addActionListener(e -> removeSelectedItem());
        btnClearCart.addActionListener(e -> clearCart());

        cartButtonPanel.add(btnRemove);
        cartButtonPanel.add(btnClearCart);

        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(cartPanel, BorderLayout.CENTER);

        // Right panel - Payment
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Pembayaran"));
        paymentPanel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Customer
        gbc.gridx = 0;
        gbc.gridy = 0;
        paymentPanel.add(new JLabel("Customer:"), gbc);

        txtCustomerName = new JTextField();
        gbc.gridx = 1;
        paymentPanel.add(txtCustomerName, gbc);

        // Payment method
        gbc.gridx = 0;
        gbc.gridy = 1;
        paymentPanel.add(new JLabel("Pembayaran:"), gbc);

        cmbPayment = new JComboBox<>(new String[]{"CASH", "QRIS", "CREDIT"});
        gbc.gridx = 1;
        paymentPanel.add(cmbPayment, gbc);

        // Discount
        gbc.gridx = 0;
        gbc.gridy = 2;
        paymentPanel.add(new JLabel("Diskon (Rp):"), gbc);

        txtDiscount = new JTextField("0");
        gbc.gridx = 1;
        paymentPanel.add(txtDiscount, gbc);

        // Tax
        gbc.gridx = 0;
        gbc.gridy = 3;
        paymentPanel.add(new JLabel("Pajak (%):"), gbc);

        txtTax = new JTextField("0");
        gbc.gridx = 1;
        paymentPanel.add(txtTax, gbc);

        // Amount paid
        gbc.gridx = 0;
        gbc.gridy = 4;
        paymentPanel.add(new JLabel("Bayar (Rp):"), gbc);

        txtAmountPaid = new JTextField();
        gbc.gridx = 1;
        paymentPanel.add(txtAmountPaid, gbc);

        // Totals display
        gbc.gridx = 0;
        gbc.gridy = 5;
        paymentPanel.add(new JLabel("Subtotal:"), gbc);

        lblSubtotal = new JLabel("Rp 0");
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 1;
        paymentPanel.add(lblSubtotal, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        paymentPanel.add(new JLabel("Diskon:"), gbc);

        lblDiscount = new JLabel("Rp 0");
        gbc.gridx = 1;
        paymentPanel.add(lblDiscount, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        paymentPanel.add(new JLabel("Pajak:"), gbc);

        lblTax = new JLabel("Rp 0");
        gbc.gridx = 1;
        paymentPanel.add(lblTax, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        paymentPanel.add(new JLabel("TOTAL:"), gbc);

        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(Color.BLUE);
        gbc.gridx = 1;
        paymentPanel.add(lblTotal, gbc);

        // Calculate button
        JButton btnCalculate = new JButton("Hitung Total");
        btnCalculate.addActionListener(e -> calculateTotals());
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        paymentPanel.add(btnCalculate, gbc);

        // Process button
        JButton btnProcess = new JButton("PROSES TRANSAKSI");
        btnProcess.setBackground(new Color(0, 150, 0));
        btnProcess.setForeground(Color.WHITE);
        btnProcess.setFont(new Font("Arial", Font.BOLD, 14));
        btnProcess.addActionListener(e -> processSale());
        gbc.gridy = 10;
        paymentPanel.add(btnProcess, gbc);

        // Close button
        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener(e -> dispose());
        gbc.gridy = 11;
        paymentPanel.add(btnClose, gbc);

        mainPanel.add(paymentPanel, BorderLayout.EAST);

        add(mainPanel);

        // Enter key listeners
        txtBarcode.addActionListener(e -> addToCart());
        txtQuantity.addActionListener(e -> addToCart());

        // Set default button
        getRootPane().setDefaultButton(btnAdd);
    }

    private void addToCart() {
        String code = txtBarcode.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Masukkan kode produk!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtBarcode.requestFocus();
            return;
        }

        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantity harus lebih dari 0!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                txtQuantity.selectAll();
                txtQuantity.requestFocus();
                return;
            }

            // Check product existence
            Product product = null;
            try {
                product = productController.getProductByCode(code);
            } catch (Exception e) {
                // Fallback: create dummy product for testing
                product = createDummyProduct(code);
            }

            if (product == null) {
                JOptionPane.showMessageDialog(this,
                        "Produk tidak ditemukan! Menggunakan produk dummy untuk testing.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                product = createDummyProduct(code);
            }

            // Check stock (skip for dummy products)
            if (product.getId() > 0) { // Real product
                if (!productController.checkStock(product.getId(), quantity)) {
                    JOptionPane.showMessageDialog(this,
                            "Stok tidak mencukupi! Stok tersedia: " + product.getStock(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    txtQuantity.selectAll();
                    txtQuantity.requestFocus();
                    return;
                }
            }

            // Check if product already in cart
            for (SaleItem item : cartItems) {
                if (item.getProduct().getCode().equals(code)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    updateCartTable();
                    clearInput();
                    calculateTotals();
                    return;
                }
            }

            // Add new item to cart
            SaleItem saleItem = new SaleItem(product.getId(), quantity, product.getPrice());
            saleItem.setProduct(product);
            cartItems.add(saleItem);

            updateCartTable();
            clearInput();
            calculateTotals();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Quantity harus angka!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtQuantity.selectAll();
            txtQuantity.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Product createDummyProduct(String code) {
        Product product = new Product();
        product.setId(0); // ID 0 for dummy
        product.setCode(code);
        product.setName("Produk " + code);
        product.setUnit("Pcs");
        product.setPrice(10000);
        product.setStock(100);
        return product;
    }

    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih item yang akan dihapus!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cartItems.remove(selectedRow);
        updateCartTable();
        calculateTotals();
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        for (SaleItem item : cartItems) {
            String productCode = item.getProduct() != null ? item.getProduct().getCode() : "N/A";
            String productName = item.getProduct() != null ? item.getProduct().getName() : "Produk";

            Object[] row = {
                    productCode,
                    productName,
                    item.getPrice(),
                    item.getQuantity(),
                    item.getLineTotal()
            };
            cartTableModel.addRow(row);
        }
    }

    private void calculateTotals() {
        double subtotal = cartItems.stream()
                .mapToDouble(SaleItem::getLineTotal)
                .sum();

        double discount = 0;
        double tax = 0;

        try {
            discount = Double.parseDouble(txtDiscount.getText());
            tax = Double.parseDouble(txtTax.getText());
        } catch (NumberFormatException e) {
            // Use default values
        }

        double afterDiscount = subtotal - discount;
        double taxAmount = afterDiscount * (tax / 100);
        double total = afterDiscount + taxAmount;

        lblSubtotal.setText(currencyFormat.format(subtotal));
        lblDiscount.setText(currencyFormat.format(discount));
        lblTax.setText(currencyFormat.format(taxAmount));
        lblTotal.setText(currencyFormat.format(total));
    }

    private void processSale() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Keranjang kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Ambil Input dari Form
            double amountPaid = 0;
            double discount = 0;
            double tax = 0;

            try {
                String paidStr = txtAmountPaid.getText().replaceAll("[^0-9.]", ""); // Bersihkan format currency jika ada
                String discStr = txtDiscount.getText().replaceAll("[^0-9.]", "");
                String taxStr = txtTax.getText().replaceAll("[^0-9.]", "");

                amountPaid = paidStr.isEmpty() ? 0 : Double.parseDouble(paidStr);
                discount = discStr.isEmpty() ? 0 : Double.parseDouble(discStr);
                tax = taxStr.isEmpty() ? 0 : Double.parseDouble(taxStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Format angka salah!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validasi Pembayaran
            double currentTotal = calculateCurrentTotalValue(discount, tax);
            if (amountPaid < currentTotal) {
                JOptionPane.showMessageDialog(this,
                        String.format("Uang kurang! Total: Rp %,.0f, Bayar: Rp %,.0f", currentTotal, amountPaid),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String paymentMethod = (String) cmbPayment.getSelectedItem();

            // TODO: Integrasikan dengan Customer jika ada fitur pilih customer
            // Untuk sekarang set 0 (Customer Umum) atau ambil dari component jika sudah dibuat
            int customerId = 0;

            // 2. PANGGIL CONTROLLER (Inilah jembatan ke database)
            // Kita kirim cartItems clone agar list asli aman
            List<SaleItem> itemsToSave = new ArrayList<>(cartItems);

            Sale savedSale = saleController.createSale(
                    itemsToSave,
                    paymentMethod,
                    amountPaid,
                    customerId,
                    discount,
                    tax
            );

            // 3. Cek Hasil
            if (savedSale != null) {
                // Berhasil disimpan ke DB!

                // Generate receipt text
                String receipt = ReceiptGenerator.generateReceipt(savedSale);

                // Show receipt dialog
                showReceiptDialog(receipt, savedSale);

                // Clear UI
                clearCart();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menyimpan transaksi ke database!",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validasi Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    private double calculateCurrentTotalValue(double discount, double tax) {
        double subtotal = cartItems.stream().mapToDouble(SaleItem::getLineTotal).sum();
        double afterDiscount = subtotal - discount;
        double taxAmount = afterDiscount * (tax / 100);
        return afterDiscount + taxAmount;
    }

    private void showReceiptDialog(String receipt, Sale sale) {
        JDialog receiptDialog = new JDialog(this, "Struk Penjualan", true);
        receiptDialog.setSize(400, 600);
        receiptDialog.setLocationRelativeTo(this);

        JTextArea receiptArea = new JTextArea(receipt);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(receiptArea);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnPrint = new JButton("Print");
        JButton btnDownload = new JButton("Download");
        JButton btnClose = new JButton("Tutup");

        btnPrint.addActionListener(e -> {
            boolean printed = PrintService.printReceipt(receipt);
            if (printed) {
                JOptionPane.showMessageDialog(receiptDialog,
                        "Receipt berhasil diprint!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(receiptDialog,
                        "Gagal print receipt!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDownload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("receipt_" + sale.getReceiptNumber() + ".txt"));

            if (fileChooser.showSaveDialog(receiptDialog) == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter out = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                    out.print(receipt);
                    JOptionPane.showMessageDialog(receiptDialog,
                            "Receipt berhasil disimpan!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(receiptDialog,
                            "Error menyimpan file: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnClose.addActionListener(e -> receiptDialog.dispose());

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnDownload);
        buttonPanel.add(btnClose);

        receiptDialog.setLayout(new BorderLayout());
        receiptDialog.add(scrollPane, BorderLayout.CENTER);
        receiptDialog.add(buttonPanel, BorderLayout.SOUTH);

        receiptDialog.setVisible(true);
    }

    private void clearInput() {
        txtBarcode.setText("");
        txtQuantity.setText("1");
        txtBarcode.requestFocus();
    }

    private void clearCart() {
        cartItems.clear();
        updateCartTable();
        lblSubtotal.setText("Rp 0");
        lblDiscount.setText("Rp 0");
        lblTax.setText("Rp 0");
        lblTotal.setText("Rp 0");
        txtCustomerName.setText("");
        txtAmountPaid.setText("");
        txtDiscount.setText("0");
        txtTax.setText("0");
        cmbPayment.setSelectedIndex(0);
        clearInput();
    }
}