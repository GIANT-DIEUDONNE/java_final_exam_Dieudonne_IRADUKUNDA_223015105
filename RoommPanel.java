package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.util.DB;

public class RoommPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public RoommPanel() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Room Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        String[] columns = {"RoomID", "ReservationID", "Name", "Description", "Category", "PriceOrValue", "Status", "CreatedAt"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
      
        JPanel buttons = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        
        buttons.add(refreshBtn);
        
        add(buttons, BorderLayout.SOUTH);
      
        refreshBtn.addActionListener(e -> loadRoomData());
       

        loadRoomData();
    }
   
    private void loadRoomData() {
        model.setRowCount(0);
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM room")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("RoomID"),
                        rs.getObject("ReservationID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getString("Category"),
                        rs.getBigDecimal("PriceOrValue"),
                        rs.getString("Status"),
                        rs.getTimestamp("CreatedAt")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage());
        }
    }
    
    private void addRoom() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField catField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});

        Object[] fields = {
                "Name:", nameField,
                "Description:", descField,
                "Category:", catField,
                "PriceOrValue:", priceField,
                "Status:", statusBox
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Room", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO room (Name, Description, Category, PriceOrValue, Status) VALUES (?, ?, ?, ?, ?)")) {

                ps.setString(1, nameField.getText());
                ps.setString(2, descField.getText());
                ps.setString(3, catField.getText());
                ps.setBigDecimal(4, new java.math.BigDecimal(priceField.getText()));
                ps.setString(5, (String) statusBox.getSelectedItem());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room added successfully!");
                loadRoomData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding room: " + e.getMessage());
            }
        }
    }
  
    private void updateRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to update.");
            return;
        }

        int roomId = (int) model.getValueAt(selectedRow, 0);
        String currentName = (String) model.getValueAt(selectedRow, 2);
        String currentDesc = (String) model.getValueAt(selectedRow, 3);
        String currentCat = (String) model.getValueAt(selectedRow, 4);
        String currentPrice = model.getValueAt(selectedRow, 5).toString();
        String currentStatus = (String) model.getValueAt(selectedRow, 6);

        JTextField nameField = new JTextField(currentName);
        JTextField descField = new JTextField(currentDesc);
        JTextField catField = new JTextField(currentCat);
        JTextField priceField = new JTextField(currentPrice);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});
        statusBox.setSelectedItem(currentStatus);

        Object[] fields = {
                "Name:", nameField,
                "Description:", descField,
                "Category:", catField,
                "PriceOrValue:", priceField,
                "Status:", statusBox
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Update Room", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE room SET Name=?, Description=?, Category=?, PriceOrValue=?, Status=? WHERE RoomID=?")) {

                ps.setString(1, nameField.getText());
                ps.setString(2, descField.getText());
                ps.setString(3, catField.getText());
                ps.setBigDecimal(4, new java.math.BigDecimal(priceField.getText()));
                ps.setString(5, (String) statusBox.getSelectedItem());
                ps.setInt(6, roomId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room updated successfully!");
                loadRoomData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating room: " + e.getMessage());
            }
        }
    }
   
    private void deleteRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            return;
        }

        int roomId = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this room?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM room WHERE RoomID=?")) {
                ps.setInt(1, roomId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                loadRoomData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting room: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Room Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(950, 550);
            frame.setLocationRelativeTo(null);
            frame.add(new RoomPanel());
            frame.setVisible(true);
        });
    }
}
