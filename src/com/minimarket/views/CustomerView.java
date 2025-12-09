package com.minimarket.views;

import com.minimarket.controllers.CustomerController;
import com.minimarket.models.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
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
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(80);

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
        SwingUtilities.invokeLater(() -> {
            try {
                // Untuk testing, gunakan data dummy
                List<Customer> customers = getDummyCustomers();
                tableModel.setRowCount(0);

                for (Customer customer : customers) {
                    Object[] row = {
                            customer.getId(),
                            customer.getName(),
                            customer.getPhone(),
                            customer.getEmail() != null ? customer.getEmail() : "",
                            customer.getAddress() != null ? customer.getAddress() : "",
                            customer.getPoints()
                    };
                    tableModel.addRow(row);
                }

                txtSearch.setText("");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading customers: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    private List<Customer> getDummyCustomers() {
        List<Customer> customers = new ArrayList<>();

        // Tambah beberapa customer dummy
        Customer c1 = new Customer();
        c1.setId(1);
        c1.setName("Budi Santoso");
        c1.setPhone("081234567890");
        c1.setEmail("budi@gmail.com");
        c1.setAddress("Jl. Merdeka No. 123");
        c1.setPoints(150);
        customers.add(c1);

        Customer c2 = new Customer();
        c2.setId(2);
        c2.setName("Siti Aminah");
        c2.setPhone("081298765432");
        c2.setEmail("siti@gmail.com");
        c2.setAddress("Jl. Sudirman No. 45");
        c2.setPoints(80);
        customers.add(c2);

        Customer c3 = new Customer();
        c3.setId(3);
        c3.setName("Agus Wijaya");
        c3.setPhone("081312345678");
        c3.setAddress("Jl. Gatot Subroto No. 67");
        c3.setPoints(200);
        customers.add(c3);

        return customers;
    }

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadCustomers();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                List<Customer> allCustomers = getDummyCustomers();
                List<Customer> filteredCustomers = new ArrayList<>();

                for (Customer customer : allCustomers) {
                    if (customer.getName().toLowerCase().contains(keyword) ||
                            (customer.getPhone() != null && customer.getPhone().contains(keyword)) ||
                            (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(keyword))) {
                        filteredCustomers.add(customer);
                    }
                }

                tableModel.setRowCount(0);
                for (Customer customer : filteredCustomers) {
                    Object[] row = {
                            customer.getId(),
                            customer.getName(),
                            customer.getPhone(),
                            customer.getEmail() != null ? customer.getEmail() : "",
                            customer.getAddress() != null ? customer.getAddress() : "",
                            customer.getPoints()
                    };
                    tableModel.addRow(row);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error searching customers: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addCustomer() {
        CustomerDialog dialog = new CustomerDialog(this, true);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Customer customer = dialog.getCustomer();
            try {
                // Untuk testing, langsung tambah ke table
                List<Customer> currentCustomers = getDummyCustomers();
                customer.setId(currentCustomers.size() + 1);

                JOptionPane.showMessageDialog(this,
                        "Customer berhasil ditambahkan! (Simulasi)",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers();

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void editCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih customer yang akan diedit!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil data customer dari row yang dipilih
        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);
        String customerPhone = (String) tableModel.getValueAt(selectedRow, 2);
        String customerEmail = (String) tableModel.getValueAt(selectedRow, 3);
        String customerAddress = (String) tableModel.getValueAt(selectedRow, 4);

        // Buat customer object
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName(customerName);
        customer.setPhone(customerPhone);
        customer.setEmail(customerEmail);
        customer.setAddress(customerAddress);

        CustomerDialog dialog = new CustomerDialog(this, true, customer);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Customer updatedCustomer = dialog.getCustomer();
            JOptionPane.showMessageDialog(this,
                    "Customer berhasil diupdate! (Simulasi)",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCustomers();
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih customer yang akan dihapus!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus customer '" + customerName + "'?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                JOptionPane.showMessageDialog(this,
                        "Customer berhasil dihapus! (Simulasi)",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

// Customer Dialog Class
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
        txtEmail.setText(customer.getEmail() != null ? customer.getEmail() : "");
        txtAddress.setText(customer.getAddress() != null ? customer.getAddress() : "");
    }

    private void saveCustomer() {
        try {
            // Validate inputs
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

            // Update customer object
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