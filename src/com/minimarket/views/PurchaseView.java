package com.minimarket.views;

import com.minimarket.controllers.PurchaseController;
import javax.swing.*;
import java.awt.*;

public class PurchaseView extends JFrame {
    private PurchaseController purchaseController;

    public PurchaseView() {
        purchaseController = new PurchaseController();
        initUI();
    }

    private void initUI() {
        setTitle("Manajemen Pembelian");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("Manajemen Pembelian", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Content
        JLabel lblContent = new JLabel("Fitur pembelian akan diimplementasikan segera", SwingConstants.CENTER);
        mainPanel.add(lblContent, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnClose);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}