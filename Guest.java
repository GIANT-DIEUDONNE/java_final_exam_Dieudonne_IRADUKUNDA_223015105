package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DB; // Make sure this returns a valid Connection

public class Guest extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public Guest() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Guest Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Table columns
        String[] columns = {"GuestID", "Username", "FullName", "Email", "Role", "CreatedAt", "LastLogin"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh Button
        JPanel buttons = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);

        // Load data initially
        loadGuestData();

        // Button Action
        refreshBtn.addActionListener(e -> loadGuestData());
    }

    // Load all guests
    private void loadGuestData() {
        model.setRowCount(0); // clear table
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT GuestID, Username, FullName, Email, Role, CreatedAt, LastLogin FROM guest")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("GuestID"),
                        rs.getString("Username"),
                        rs.getString("FullName"),
                        rs.getString("Email"),
                        rs.getString("Role"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getTimestamp("LastLogin")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading guests: " + e.getMessage());
        }
    }
}
