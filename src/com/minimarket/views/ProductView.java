package com.minimarket.views;

import com.minimarket.controllers.ProductController;
import com.minimarket.models.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductView extends JFrame {
    private ProductController productController;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JTextField txtSearch;

    public ProductView() {
        productController = new ProductController();
        initUI();
        loadProducts();
    }

    private void initUI() {
        setTitle("Manajemen Produk");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Cari:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(e -> searchProducts());
        searchPanel.add(btnSearch);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadProducts());
        searchPanel.add(btnRefresh);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Daftar Produk"));

        String[] columns = {"ID", "Kode", "Nama", "Unit", "Harga", "Stok", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 4) return Double.class;
                if (columnIndex == 5) return Integer.class;
                return String.class;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(productTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAdd = new JButton("Tambah Produk");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClose = new JButton("Tutup");

        btnAdd.addActionListener(e -> addProduct());
        btnEdit.addActionListener(e -> editProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadProducts() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Product> products = productController.getAllProducts();
                tableModel.setRowCount(0);

                for (Product product : products) {
                    String status = "NORMAL";
                    if (product.getStock() == 0) {
                        status = "HABIS";
                    } else if (product.getStock() <= 10) {
                        status = "RENDAH";
                    }

                    Object[] row = {
                            product.getId(),
                            product.getCode(),
                            product.getName(),
                            product.getUnit(),
                            product.getPrice(),
                            product.getStock(),
                            status
                    };
                    tableModel.addRow(row);
                }

                txtSearch.setText("");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading products: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                List<Product> products = productController.searchProducts(keyword);
                tableModel.setRowCount(0);

                for (Product product : products) {
                    String status = "NORMAL";
                    if (product.getStock() == 0) {
                        status = "HABIS";
                    } else if (product.getStock() <= 10) {
                        status = "RENDAH";
                    }

                    Object[] row = {
                            product.getId(),
                            product.getCode(),
                            product.getName(),
                            product.getUnit(),
                            product.getPrice(),
                            product.getStock(),
                            status
                    };
                    tableModel.addRow(row);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error searching products: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addProduct() {
        ProductDialog dialog = new ProductDialog(this, true);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Product product = dialog.getProduct();
            try {
                if (productController.addProduct(product)) {
                    JOptionPane.showMessageDialog(this,
                            "Produk berhasil ditambahkan!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menambahkan produk!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih produk yang akan diedit!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = productController.getProductById(productId);

        if (product != null) {
            ProductDialog dialog = new ProductDialog(this, true, product);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                Product updatedProduct = dialog.getProduct();
                try {
                    if (productController.updateProduct(updatedProduct)) {
                        JOptionPane.showMessageDialog(this,
                                "Produk berhasil diupdate!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadProducts();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Gagal mengupdate produk!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this,
                            e.getMessage(),
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih produk yang akan dihapus!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus produk '" + productName + "'?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (productController.deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this,
                            "Produk berhasil dihapus!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menghapus produk!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

// Product Dialog Class
class ProductDialog extends JDialog {
    private Product product;
    private boolean saved = false;

    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtUnit;
    private JTextField txtPrice;
    private JTextField txtStock;
    private JTextField txtCategory;
    private JTextField txtBrand;

    public ProductDialog(Frame parent, boolean modal) {
        this(parent, modal, null);
    }

    public ProductDialog(Frame parent, boolean modal, Product existingProduct) {
        super(parent, modal);
        this.product = existingProduct != null ? existingProduct : new Product();

        setTitle(existingProduct != null ? "Edit Produk" : "Tambah Produk");
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        if (existingProduct != null) {
            populateFields();
        }
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Kode*:"), gbc);

        txtCode = new JTextField();
        gbc.gridx = 1;
        panel.add(txtCode, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nama*:"), gbc);

        txtName = new JTextField();
        gbc.gridx = 1;
        panel.add(txtName, gbc);

        // Unit
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Unit:"), gbc);

        txtUnit = new JTextField();
        gbc.gridx = 1;
        panel.add(txtUnit, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Harga*:"), gbc);

        txtPrice = new JTextField();
        gbc.gridx = 1;
        panel.add(txtPrice, gbc);

        // Stock
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Stok:"), gbc);

        txtStock = new JTextField("0");
        gbc.gridx = 1;
        panel.add(txtStock, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Kategori:"), gbc);

        txtCategory = new JTextField();
        gbc.gridx = 1;
        panel.add(txtCategory, gbc);

        // Brand
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Merk:"), gbc);

        txtBrand = new JTextField();
        gbc.gridx = 1;
        panel.add(txtBrand, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Simpan");
        JButton btnCancel = new JButton("Batal");

        btnSave.addActionListener(e -> saveProduct());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel);

        // Set default button
        getRootPane().setDefaultButton(btnSave);
    }

    private void populateFields() {
        txtCode.setText(product.getCode());
        txtName.setText(product.getName());
        txtUnit.setText(product.getUnit());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtStock.setText(String.valueOf(product.getStock()));
        txtCategory.setText(product.getCategory());
        txtBrand.setText(product.getBrand());
    }

    private void saveProduct() {
        try {
            // Validate inputs
            if (txtCode.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kode produk harus diisi!");
                txtCode.requestFocus();
                return;
            }

            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama produk harus diisi!");
                txtName.requestFocus();
                return;
            }

            double price = Double.parseDouble(txtPrice.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!");
                txtPrice.requestFocus();
                return;
            }

            int stock = Integer.parseInt(txtStock.getText());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif!");
                txtStock.requestFocus();
                return;
            }

            // Update product object
            product.setCode(txtCode.getText().trim());
            product.setName(txtName.getText().trim());
            product.setUnit(txtUnit.getText().trim());
            product.setPrice(price);
            product.setStock(stock);
            product.setCategory(txtCategory.getText().trim());
            product.setBrand(txtBrand.getText().trim());

            saved = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Harga dan Stok harus angka!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Product getProduct() {
        return product;
    }

    public boolean isSaved() {
        return saved;
    }
}