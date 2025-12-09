package com.minimarket.views;

import com.minimarket.controllers.UserController;
import com.minimarket.models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserView extends JFrame {
    private UserController userController;
    private DefaultTableModel tableModel;
    private JTable userTable;

    public UserView() {
        userController = new UserController();
        initUI();
        loadUsers();
    }

    private void initUI() {
        setTitle("Manajemen User");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"ID", "Nama", "Email", "Role", "Password"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        userTable = new JTable(tableModel);
        // Sembunyikan kolom password agar tidak terlihat langsung
        userTable.getColumnModel().getColumn(4).setMinWidth(0);
        userTable.getColumnModel().getColumn(4).setMaxWidth(0);
        userTable.getColumnModel().getColumn(4).setWidth(0);

        mainPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showUserDialog(null));
        btnEdit.addActionListener(e -> editSelectedUser());
        btnDelete.addActionListener(e -> deleteSelectedUser());
        btnRefresh.addActionListener(e -> loadUsers());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userController.getAllUsers();
        for (User u : users) {
            // Password disembunyikan di tampilan tabel (****)
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getPassword()});
        }
    }

    private void editSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user dulu!");
            return;
        }
        User u = new User();
        u.setId((int) tableModel.getValueAt(row, 0));
        u.setName((String) tableModel.getValueAt(row, 1));
        u.setEmail((String) tableModel.getValueAt(row, 2));
        u.setRole((String) tableModel.getValueAt(row, 3));
        // Password asli tidak diambil dari tabel untuk keamanan, dibiarkan null
        showUserDialog(u);
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                if (userController.deleteUser(id)) {
                    loadUsers();
                    JOptionPane.showMessageDialog(this, "User dihapus!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUserDialog(User existing) {
        JDialog dialog = new JDialog(this, existing == null ? "Tambah User" : "Edit User", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtName = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JPasswordField txtPass = new JPasswordField(20);
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"admin", "cashier", "manager"});

        if (existing != null) {
            txtName.setText(existing.getName());
            txtEmail.setText(existing.getEmail());
            cmbRole.setSelectedItem(existing.getRole());
            // Password dibiarkan kosong saat edit (hanya diisi jika ingin diubah)
        }

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1; dialog.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; dialog.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; dialog.add(cmbRole, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel(existing == null ? "Password*:" : "Password (Opsional):"), gbc);
        gbc.gridx = 1; dialog.add(txtPass, gbc);

        if (existing != null) {
            gbc.gridy = 4; gbc.gridx = 1;
            JLabel lblHint = new JLabel("<html><font size='2' color='gray'>Kosongkan jika tidak ingin ubah password</font></html>");
            dialog.add(lblHint, gbc);
        }

        JButton btnSave = new JButton("Simpan");
        btnSave.addActionListener(e -> {
            try {
                User u = existing == null ? new User() : existing;
                u.setName(txtName.getText());
                u.setEmail(txtEmail.getText());
                u.setRole((String) cmbRole.getSelectedItem());
                u.setPassword(new String(txtPass.getPassword()));

                boolean success = existing == null ? userController.addUser(u) : userController.updateUser(u);
                if (success) {
                    loadUsers();
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