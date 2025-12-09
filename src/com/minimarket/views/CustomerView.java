package com.minimarket.views;

import com.minimarket.controllers.CustomerController;
import com.minimarket.models.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerView extends JFrame {
    private CustomerController customerController;
    private DefaultTableModel tableModel;
    private JTable customerTable;
    private JTextField txtSearch;

    public CustomerView() {
        customerController = new CustomerController();
        initUI();
        loadCustomers();
    }

    private void initUI() {
        setTitle("Manajemen Customer");
        setSize(800, 500);
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
        btnSearch.addActionListener(e -> searchCustomers());
        searchPanel.add(btnSearch);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadCustomers());
        searchPanel.add(btnRefresh);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Daftar Customer"));

        String[] columns = {"ID", "Nama", "Telepon", "Email", "Alamat", "Points"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAdd = new JButton("Tambah Customer");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClose = new JButton("Tutup");

        btnAdd.addActionListener(e -> addCustomer());
        btnEdit.addActionListener(e -> editCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            tableModel.setRowCount(0);

            for (Customer customer : customers) {
                Object[] row = {
                        customer.getId(),
                        customer.getName(),
                        customer.getPhone(),
                        customer.getEmail(),
                        customer.getAddress(),
                        customer.getPoints()
                };
                tableModel.addRow(row);
            }

            txtSearch.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading customers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCustomers() {
        // Implementation for searching customers
        // This would depend on your CustomerService implementation
    }

    private void addCustomer() {
        CustomerDialog dialog = new CustomerDialog(this, true);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Customer customer = dialog.getCustomer();
            try {
                if (customerController.addCustomer(customer)) {
                    JOptionPane.showMessageDialog(this,
                            "Customer berhasil ditambahkan!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomers();
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void editCustomer() {
        // Implementation for editing customer
    }

    private void deleteCustomer() {
        // Implementation for deleting customer
    }
}

class CustomerDialog extends JDialog {
    private Customer customer;
    private boolean saved = false;

    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;

    public CustomerDialog(Frame parent, boolean modal) {
        this(parent, modal, null);
    }

    public CustomerDialog(Frame parent, boolean modal, Customer existingCustomer) {
        super(parent, modal);
        this.customer = existingCustomer != null ? existingCustomer : new Customer();

        setTitle(existingCustomer != null ? "Edit Customer" : "Tambah Customer");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        if (existingCustomer != null) {
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

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nama*:"), gbc);

        txtName = new JTextField();
        gbc.gridx = 1;
        panel.add(txtName, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Telepon*:"), gbc);

        txtPhone = new JTextField();
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);

        txtEmail = new JTextField();
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Alamat:"), gbc);

        txtAddress = new JTextField();
        gbc.gridx = 1;
        panel.add(txtAddress, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Simpan");
        JButton btnCancel = new JButton("Batal");

        btnSave.addActionListener(e -> saveCustomer());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void populateFields() {
        txtName.setText(customer.getName());
        txtPhone.setText(customer.getPhone());
        txtEmail.setText(customer.getEmail());
        txtAddress.setText(customer.getAddress());
    }

    private void saveCustomer() {
        try {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama harus diisi!");
                txtName.requestFocus();
                return;
            }

            if (txtPhone.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Telepon harus diisi!");
                txtPhone.requestFocus();
                return;
            }

            customer.setName(txtName.getText().trim());
            customer.setPhone(txtPhone.getText().trim());
            customer.setEmail(txtEmail.getText().trim());
            customer.setAddress(txtAddress.getText().trim());

            saved = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isSaved() {
        return saved;
    }
}