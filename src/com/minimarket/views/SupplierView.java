package com.minimarket.views;

import com.minimarket.controllers.SupplierController;
import com.minimarket.models.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierView extends JFrame {
    private SupplierController supplierController;
    private DefaultTableModel tableModel;
    private JTable supplierTable;

    public SupplierView() {
        supplierController = new SupplierController();
        initUI();
        loadSuppliers();
    }

    private void initUI() {
        setTitle("Manajemen Supplier");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table Panel
        String[] columns = {"ID", "Kode", "Nama", "Telepon", "Email", "Alamat"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        supplierTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showSupplierDialog(null));
        btnEdit.addActionListener(e -> editSelectedSupplier());
        btnDelete.addActionListener(e -> deleteSelectedSupplier());
        btnRefresh.addActionListener(e -> loadSuppliers());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = supplierController.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{s.getId(), s.getCode(), s.getName(), s.getPhone(), s.getEmail(), s.getAddress()});
        }
    }

    private void editSelectedSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih supplier dulu!");
            return;
        }
        Supplier s = new Supplier();
        s.setId((int) tableModel.getValueAt(row, 0));
        s.setCode((String) tableModel.getValueAt(row, 1));
        s.setName((String) tableModel.getValueAt(row, 2));
        s.setPhone((String) tableModel.getValueAt(row, 3));
        s.setEmail((String) tableModel.getValueAt(row, 4));
        s.setAddress((String) tableModel.getValueAt(row, 5));
        showSupplierDialog(s);
    }

    private void deleteSelectedSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih supplier dulu!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            if (supplierController.deleteSupplier(id)) {
                loadSuppliers();
                JOptionPane.showMessageDialog(this, "Berhasil dihapus!");
            }
        }
    }

    private void showSupplierDialog(Supplier existing) {
        JDialog dialog = new JDialog(this, existing == null ? "Tambah Supplier" : "Edit Supplier", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtCode = new JTextField(20);
        JTextField txtName = new JTextField(20);
        JTextField txtPhone = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextArea txtAddress = new JTextArea(3, 20);

        if (existing != null) {
            txtCode.setText(existing.getCode());
            txtCode.setEditable(false); // Kode tidak boleh diubah saat edit
            txtName.setText(existing.getName());
            txtPhone.setText(existing.getPhone());
            txtEmail.setText(existing.getEmail());
            txtAddress.setText(existing.getAddress());
        }

        // Komponen Form
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Kode:"), gbc);
        gbc.gridx = 1; dialog.add(txtCode, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1; dialog.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Telepon:"), gbc);
        gbc.gridx = 1; dialog.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; dialog.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; dialog.add(new JScrollPane(txtAddress), gbc);

        JButton btnSave = new JButton("Simpan");
        btnSave.addActionListener(e -> {
            try {
                Supplier s = existing == null ? new Supplier() : existing;
                s.setCode(txtCode.getText());
                s.setName(txtName.getText());
                s.setPhone(txtPhone.getText());
                s.setEmail(txtEmail.getText());
                s.setAddress(txtAddress.getText());

                boolean success = existing == null ? supplierController.addSupplier(s) : supplierController.updateSupplier(s);
                if (success) {
                    loadSuppliers();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Berhasil disimpan!");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        gbc.gridx = 1; gbc.gridy = 5; dialog.add(btnSave, gbc);
        dialog.setVisible(true);
    }
}