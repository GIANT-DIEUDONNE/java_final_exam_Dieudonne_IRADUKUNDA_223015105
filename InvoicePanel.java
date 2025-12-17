package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DB;

public class InvoicePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public InvoicePanel() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("INVOICE PANEL", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Updated table columns
        String[] columns = {"InvoiceID", "StaffID", "RoomName", "Amount", "Date", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel();
        JButton createBtn = new JButton("Create Invoice");
        JButton updateBtn = new JButton("Update Invoice");
        JButton deleteBtn = new JButton("Delete Invoice");
        JButton refreshBtn = new JButton("Refresh");

        buttons.add(createBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);

        add(buttons, BorderLayout.SOUTH);

        // Load data initially
        loadInvoiceData();

        // Button actions
        refreshBtn.addActionListener(e -> loadInvoiceData());
        createBtn.addActionListener(e -> createInvoice());
        updateBtn.addActionListener(e -> updateInvoice());
        deleteBtn.addActionListener(e -> deleteInvoice());
    }

    // Load invoices (with room name + price)
    private void loadInvoiceData() {
        model.setRowCount(0);
        String query = """
            SELECT i.InvoiceID, i.StaffID, r.Name AS RoomName, 
                   r.PriceOrValue AS Amount, i.Date, i.Status
            FROM invoice i
            LEFT JOIN room r ON i.RoomID = r.RoomID
        """;
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("InvoiceID"),
                        rs.getInt("StaffID"),
                        rs.getString("RoomName"),
                        rs.getDouble("Amount"),
                        rs.getDate("Date"),
                        rs.getString("Status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading invoices: " + e.getMessage());
        }
    }

    // Create new invoice (Amount auto-fills from room)
    private void createInvoice() {
        JTextField staffField = new JTextField();
        JTextField dateField = new JTextField("yyyy-mm-dd");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending", "Paid", "Cancelled"});

        // Room combo box (with automatic amount display)
        JComboBox<String> roomBox = new JComboBox<>();
        JLabel amountLabel = new JLabel("Amount: ");

        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT Name FROM room")) {
            while (rs.next()) {
                roomBox.addItem(rs.getString("Name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage());
        }

        // When a room is selected, show its price
        roomBox.addActionListener(e -> {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT PriceOrValue FROM room WHERE Name=?")) {
                ps.setString(1, roomBox.getSelectedItem().toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    amountLabel.setText("Amount: " + rs.getDouble("PriceOrValue"));
                }
            } catch (Exception ex) {
                amountLabel.setText("Amount: Error");
            }
        });

        Object[] inputs = {
                "StaffID:", staffField,
                "Room:", roomBox,
                amountLabel,
                "Date:", dateField,
                "Status:", statusBox
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Create Invoice", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection()) {
                // Get RoomID and Price from selected room
                int roomID = 0;
                double amount = 0.0;
                try (PreparedStatement psRoom = con.prepareStatement("SELECT RoomID, PriceOrValue FROM room WHERE Name=?")) {
                    psRoom.setString(1, roomBox.getSelectedItem().toString());
                    ResultSet rs = psRoom.executeQuery();
                    if (rs.next()) {
                        roomID = rs.getInt("RoomID");
                        amount = rs.getDouble("PriceOrValue");
                    }
                }

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO invoice (StaffID, RoomID, Amount, Date, Status) VALUES (?, ?, ?, ?, ?)");
                ps.setInt(1, Integer.parseInt(staffField.getText()));
                ps.setInt(2, roomID);
                ps.setDouble(3, amount); // auto from room
                ps.setDate(4, java.sql.Date.valueOf(dateField.getText()));
                ps.setString(5, statusBox.getSelectedItem().toString());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Invoice created successfully!");
                loadInvoiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error creating invoice: " + e.getMessage());
            }
        }
    }

    // Update selected invoice
    private void updateInvoice() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an invoice to update");
            return;
        }

        int invoiceID = (int) model.getValueAt(selectedRow, 0);
        int currentStaffID = (int) model.getValueAt(selectedRow, 1);
        String currentRoom = (String) model.getValueAt(selectedRow, 2);
        double currentAmount = (double) model.getValueAt(selectedRow, 3);
        Date currentDate = (Date) model.getValueAt(selectedRow, 4);
        String currentStatus = (String) model.getValueAt(selectedRow, 5);

        JTextField staffField = new JTextField(String.valueOf(currentStaffID));
        JTextField dateField = new JTextField(currentDate.toString());
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending", "Paid", "Cancelled"});
        statusBox.setSelectedItem(currentStatus);

        JComboBox<String> roomBox = new JComboBox<>();
        JLabel amountLabel = new JLabel("Amount: " + currentAmount);
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT Name FROM room")) {
            while (rs.next()) {
                roomBox.addItem(rs.getString("Name"));
            }
            roomBox.setSelectedItem(currentRoom);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage());
        }

        // Update amount when room changes
        roomBox.addActionListener(e -> {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT PriceOrValue FROM room WHERE Name=?")) {
                ps.setString(1, roomBox.getSelectedItem().toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    amountLabel.setText("Amount: " + rs.getDouble("PriceOrValue"));
                }
            } catch (Exception ex) {
                amountLabel.setText("Amount: Error");
            }
        });

        Object[] inputs = {
                "StaffID:", staffField,
                "Room:", roomBox,
                amountLabel,
                "Date:", dateField,
                "Status:", statusBox
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Update Invoice", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection()) {
                int roomID = 0;
                double amount = 0.0;
                try (PreparedStatement psRoom = con.prepareStatement("SELECT RoomID, PriceOrValue FROM room WHERE Name=?")) {
                    psRoom.setString(1, roomBox.getSelectedItem().toString());
                    ResultSet rs = psRoom.executeQuery();
                    if (rs.next()) {
                        roomID = rs.getInt("RoomID");
                        amount = rs.getDouble("PriceOrValue");
                    }
                }

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE invoice SET StaffID=?, RoomID=?, Amount=?, Date=?, Status=? WHERE InvoiceID=?");
                ps.setInt(1, Integer.parseInt(staffField.getText()));
                ps.setInt(2, roomID);
                ps.setDouble(3, amount);
                ps.setDate(4, java.sql.Date.valueOf(dateField.getText()));
                ps.setString(5, statusBox.getSelectedItem().toString());
                ps.setInt(6, invoiceID);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Invoice updated successfully!");
                loadInvoiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating invoice: " + e.getMessage());
            }
        }
    }

    // Delete invoice
    private void deleteInvoice() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an invoice to delete");
            return;
        }

        int invoiceID = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this invoice?", "Delete Invoice", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM invoice WHERE InvoiceID=?")) {
                ps.setInt(1, invoiceID);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Invoice deleted successfully!");
                loadInvoiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting invoice: " + e.getMessage());
            }
        }
    }
}
