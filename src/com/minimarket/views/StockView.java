package com.minimarket.views;

import com.minimarket.controllers.StockController;
import com.minimarket.controllers.ProductController;
import com.minimarket.models.StockOpname;
import com.minimarket.models.Product;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StockView extends JFrame {
    private StockController stockController;
    private ProductController productController;
    private JComboBox<Product> cmbProduct;
    private JTextField txtSystemStock;
    private JTextField txtPhysicalStock;
    private JTextField txtDifference;
    private JTextArea txtNotes;

    public StockView() {
        stockController = new StockController();
        productController = new ProductController();
        initUI();
        loadProducts();
    }

    private void initUI() {
        setTitle("Stock Opname");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Input Stock Opname"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Product selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Produk:"), gbc);

        cmbProduct = new JComboBox<>();
        cmbProduct.addActionListener(e -> updateSystemStock());
        gbc.gridx = 1;
        formPanel.add(cmbProduct, gbc);

        // System Stock
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Stok Sistem:"), gbc);

        txtSystemStock = new JTextField();
        txtSystemStock.setEditable(false);
        gbc.gridx = 1;
        formPanel.add(txtSystemStock, gbc);

        // Physical Stock
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Stok Fisik:"), gbc);

        txtPhysicalStock = new JTextField();
        txtPhysicalStock.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateDifference(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateDifference(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateDifference(); }
        });
        gbc.gridx = 1;
        formPanel.add(txtPhysicalStock, gbc);

        // Difference
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Selisih:"), gbc);

        txtDifference = new JTextField();
        txtDifference.setEditable(false);
        gbc.gridx = 1;
        formPanel.add(txtDifference, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Catatan:"), gbc);

        txtNotes = new JTextArea(3, 20);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        gbc.gridx = 1;
        formPanel.add(notesScroll, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnSave = new JButton("Simpan Stock Opname");
        JButton btnClear = new JButton("Clear");
        JButton btnClose = new JButton("Tutup");

        btnSave.addActionListener(e -> saveStockOpname());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadProducts() {
        try {
            List<Product> products = productController.getAllProducts();
            cmbProduct.removeAllItems();

            for (Product product : products) {
                cmbProduct.addItem(product);
            }

            if (products.size() > 0) {
                cmbProduct.setSelectedIndex(0);
                updateSystemStock();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading products: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSystemStock() {
        Product selectedProduct = (Product) cmbProduct.getSelectedItem();
        if (selectedProduct != null) {
            txtSystemStock.setText(String.valueOf(selectedProduct.getStock()));
            txtPhysicalStock.setText("");
            txtDifference.setText("");
            txtNotes.setText("");
        }
    }

    private void calculateDifference() {
        try {
            Product selectedProduct = (Product) cmbProduct.getSelectedItem();
            if (selectedProduct == null) return;

            int systemStock = selectedProduct.getStock();
            int physicalStock = Integer.parseInt(txtPhysicalStock.getText());

            int difference = physicalStock - systemStock;
            txtDifference.setText(String.valueOf(difference));

            // Color coding
            if (difference < 0) {
                txtDifference.setForeground(Color.RED);
            } else if (difference > 0) {
                txtDifference.setForeground(Color.GREEN);
            } else {
                txtDifference.setForeground(Color.BLACK);
            }

        } catch (NumberFormatException e) {
            txtDifference.setText("");
        }
    }

    private void saveStockOpname() {
        try {
            Product selectedProduct = (Product) cmbProduct.getSelectedItem();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this,
                        "Pilih produk terlebih dahulu!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int physicalStock = Integer.parseInt(txtPhysicalStock.getText());
            if (physicalStock < 0) {
                JOptionPane.showMessageDialog(this,
                        "Stok fisik tidak boleh negatif!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                txtPhysicalStock.requestFocus();
                return;
            }

            StockOpname opname = new StockOpname();
            opname.setProductId(selectedProduct.getId());
            opname.setSystemStock(selectedProduct.getStock());
            opname.setPhysicalStock(physicalStock);
            opname.setNotes(txtNotes.getText());

            // Get current user ID
            opname.setUserId(com.minimarket.controllers.LoginController.getCurrentUser().getId());

            boolean success = stockController.recordStockOpname(opname);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Stock opname berhasil disimpan!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menyimpan stock opname!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Stok fisik harus angka!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtPhysicalStock.requestFocus();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        if (cmbProduct.getItemCount() > 0) {
            cmbProduct.setSelectedIndex(0);
        }
        txtPhysicalStock.setText("");
        txtDifference.setText("");
        txtNotes.setText("");
    }
}