package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DB; // Ensure this returns a valid Connection

public class ServisePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ServisePanel() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Services Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Table columns
        String[] columns = {"ServiceID", "Name", "Description", "Category", "PriceOrValue", "Status", "CreatedAt"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);

        // Load initial data
        loadServiceData();

        // Button actions
        refreshBtn.addActionListener(e -> loadServiceData());
    }

    // Load all services from DB
    private void loadServiceData() {
        model.setRowCount(0); // Clear table
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM service")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ServiceID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getString("Category"),
                        rs.getDouble("PriceOrValue"),
                        rs.getString("Status"),
                        rs.getTimestamp("CreatedAt")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading services: " + e.getMessage());
        }
    }

    // Add service
    private void addService() {
        JTextField nameField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active","Inactive"});

        Object[] inputs = {
                "Name:", nameField,
                "Description:", new JScrollPane(descriptionArea),
                "Category:", categoryField,
                "Price:", priceField,
                "Status:", statusBox
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Add Service", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO service (Name, Description, Category, PriceOrValue, Status) VALUES (?, ?, ?, ?, ?)")) {

                ps.setString(1, nameField.getText());
                ps.setString(2, descriptionArea.getText());
                ps.setString(3, categoryField.getText());
                ps.setDouble(4, Double.parseDouble(priceField.getText()));
                ps.setString(5, statusBox.getSelectedItem().toString());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Service added successfully!");
                loadServiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding service: " + e.getMessage());
            }
        }
    }

    // Update selected service
    private void updateService() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a service to update");
            return;
        }

        int serviceID = (int) model.getValueAt(selectedRow, 0);
        String currentName = (String) model.getValueAt(selectedRow, 1);
        String currentDescription = (String) model.getValueAt(selectedRow, 2);
        String currentCategory = (String) model.getValueAt(selectedRow, 3);
        double currentPrice = (double) model.getValueAt(selectedRow, 4);
        String currentStatus = (String) model.getValueAt(selectedRow, 5);

        JTextField nameField = new JTextField(currentName);
        JTextArea descriptionArea = new JTextArea(currentDescription, 3, 20);
        JTextField categoryField = new JTextField(currentCategory);
        JTextField priceField = new JTextField(String.valueOf(currentPrice));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active","Inactive"});
        statusBox.setSelectedItem(currentStatus);

        Object[] inputs = {
                "Name:", nameField,
                "Description:", new JScrollPane(descriptionArea),
                "Category:", categoryField,
                "Price:", priceField,
                "Status:", statusBox
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Update Service", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE service SET Name=?, Description=?, Category=?, PriceOrValue=?, Status=? WHERE ServiceID=?")) {

                ps.setString(1, nameField.getText());
                ps.setString(2, descriptionArea.getText());
                ps.setString(3, categoryField.getText());
                ps.setDouble(4, Double.parseDouble(priceField.getText()));
                ps.setString(5, statusBox.getSelectedItem().toString());
                ps.setInt(6, serviceID);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Service updated successfully!");
                loadServiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating service: " + e.getMessage());
            }
        }
    }

    // Delete selected service
    private void deleteService() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a service to delete");
            return;
        }

        int serviceID = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this service?", "Delete Service", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM service WHERE ServiceID=?")) {
                ps.setInt(1, serviceID);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Service deleted successfully!");
                loadServiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting service: " + e.getMessage());
            }
        }
    }
}
