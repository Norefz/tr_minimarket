package com.minimarket.views;

import com.minimarket.controllers.InventoryController;
import com.minimarket.models.Stock;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class InventoryView extends JFrame {
    private InventoryController inventoryController;
    private DefaultTableModel tableModel;
    private JTable stockTable;

    public InventoryView() {
        inventoryController = new InventoryController();
        initUI();
        loadStocks();
    }

    private void initUI() {
        setTitle("Manajemen Batas Stok (Inventory Control)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info Panel
        JLabel lblInfo = new JLabel("<html><b>Atur Batas Stok:</b><br/>" +
                "- Min Stock: Jika stok di bawah ini, warna jadi MERAH.<br/>" +
                "- Max Stock: Batas kapasitas gudang.</html>");
        mainPanel.add(lblInfo, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Kode", "Produk", "Stok Saat Ini", "Min Stock", "Max Stock", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        stockTable = new JTable(tableModel);

        // Custom Renderer untuk mewarnai baris jika stok kritis
        stockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Ambil nilai stok dan min stock dari tabel
                try {
                    int currentStock = Integer.parseInt(table.getValueAt(row, 3).toString());
                    int minStock = Integer.parseInt(table.getValueAt(row, 4).toString());

                    if (currentStock <= minStock) {
                        c.setForeground(Color.RED);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(Color.BLACK);
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                } catch (Exception e) {}

                return c;
            }
        });

        mainPanel.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnEdit = new JButton("Atur Min/Max");
        JButton btnRefresh = new JButton("Refresh Data");

        btnEdit.addActionListener(e -> editSelectedStock());
        btnRefresh.addActionListener(e -> loadStocks());

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnRefresh);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadStocks() {
        tableModel.setRowCount(0);
        List<Stock> stocks = inventoryController.getAllStocks();
        for (Stock s : stocks) {
            String status = "Aman";
            if (s.getQuantity() <= s.getMinStock()) status = "KRITIS (Order Lagi!)";
            else if (s.getQuantity() >= s.getMaxStock()) status = "PENUH (Overstock)";

            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getProduct().getCode(),
                    s.getProduct().getName(),
                    s.getQuantity(),
                    s.getMinStock(),
                    s.getMaxStock(),
                    status
            });
        }
    }

    private void editSelectedStock() {
        int row = stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk dulu!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);
        int currentMin = (int) tableModel.getValueAt(row, 4);
        int currentMax = (int) tableModel.getValueAt(row, 5);

        JTextField txtMin = new JTextField(String.valueOf(currentMin));
        JTextField txtMax = new JTextField(String.valueOf(currentMax));

        Object[] message = {
                "Produk: " + name,
                "Batas Minimum (Peringatan Stok Rendah):", txtMin,
                "Batas Maksimum (Kapasitas Gudang):", txtMax
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Atur Batas Stok", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int newMin = Integer.parseInt(txtMin.getText());
                int newMax = Integer.parseInt(txtMax.getText());

                if (inventoryController.updateStockLevels(id, newMin, newMax)) {
                    loadStocks();
                    JOptionPane.showMessageDialog(this, "Berhasil diupdate!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}