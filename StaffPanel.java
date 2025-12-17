package com.panel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DB; 

public class StaffPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    public StaffPanel() {
        setLayout(new BorderLayout(10, 10));
        JLabel title = new JLabel("Staff Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);
        
        String[] columns = {"StaffID", "Name", "Identifier", "Status", "Location", "Contact", "AssignedSince"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
       
        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add Staff");
        JButton updateBtn = new JButton("Update Staff");
        JButton deleteBtn = new JButton("Remove Staff");
        JButton refreshBtn = new JButton("Refresh");

        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);
        
        loadStaffData();
       
        refreshBtn.addActionListener(e -> loadStaffData());
        addBtn.addActionListener(e -> addStaff());
        updateBtn.addActionListener(e -> updateStaff());
        deleteBtn.addActionListener(e -> deleteStaff());
    }
    
    private void loadStaffData() {
        model.setRowCount(0); 
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM staff")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("StaffID"),
                        rs.getString("Name"),
                        rs.getString("Identifier"),
                        rs.getString("Status"),
                        rs.getString("Location"),
                        rs.getString("Contact"),
                        rs.getDate("AssignedSince"),
                       
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading staff: " + e.getMessage());
        }
    }
    
    private void addStaff() {
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active","Inactive"});
        JTextField locationField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField assignedSinceField = new JTextField("yyyy-mm-dd");
      

        Object[] inputs = {
                "Name:", nameField,
                "Identifier:", idField,
                "Status:", statusBox,
                "Location:", locationField,
                "Contact:", contactField,
                "Assigned Since:", assignedSinceField,
               
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Add Staff", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO staff (Name, Identifier, Status, Location, Contact, AssignedSince) VALUES (?, ?, ?, ?, ?, ?)")) {

                ps.setString(1, nameField.getText());
                ps.setString(2, idField.getText());
                ps.setString(3, statusBox.getSelectedItem().toString());
                ps.setString(4, locationField.getText());
                ps.setString(5, contactField.getText());
                ps.setDate(6, java.sql.Date.valueOf(assignedSinceField.getText()));
                

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff added successfully!");
                loadStaffData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding staff: " + e.getMessage());
            }
        }
    }
    
    private void updateStaff() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff member to update");
            return;
        }

        int staffID = (int) model.getValueAt(selectedRow, 0);
        String currentName = (String) model.getValueAt(selectedRow, 1);
        String currentIdentifier = (String) model.getValueAt(selectedRow, 2);
        String currentStatus = (String) model.getValueAt(selectedRow, 3);
        String currentLocation = (String) model.getValueAt(selectedRow, 4);
        String currentContact = (String) model.getValueAt(selectedRow, 5);
        java.sql.Date currentAssignedSince = (java.sql.Date) model.getValueAt(selectedRow, 6);
    
        JTextField nameField = new JTextField(currentName);
        JTextField idField = new JTextField(currentIdentifier);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active","Inactive"});
        statusBox.setSelectedItem(currentStatus);
        JTextField locationField = new JTextField(currentLocation);
        JTextField contactField = new JTextField(currentContact);
        JTextField assignedSinceField = new JTextField(currentAssignedSince.toString());
       
        Object[] inputs = {
                "Name:", nameField,
                "Identifier:", idField,
                "Status:", statusBox,
                "Location:", locationField,
                "Contact:", contactField,
                "Assigned Since:", assignedSinceField,

        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Update Staff", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE staff SET Name=?, Identifier=?, Status=?, Location=?, Contact=?, AssignedSince=? WHERE StaffID=?")) {

                ps.setString(1, nameField.getText());
                ps.setString(2, idField.getText());
                ps.setString(3, statusBox.getSelectedItem().toString());
                ps.setString(4, locationField.getText());
                ps.setString(5, contactField.getText());
                ps.setDate(6, java.sql.Date.valueOf(assignedSinceField.getText()));
               
                ps.setInt(8, staffID);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff updated successfully!");
                loadStaffData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating staff: " + e.getMessage());
            }
        }
    }
    
    private void deleteStaff() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff member to remove");
            return;
        }

        int staffID = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this staff member?", "Delete Staff", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM staff WHERE StaffID=?")) {
                ps.setInt(1, staffID);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff removed successfully!");
                loadStaffData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error removing staff: " + e.getMessage());
            }
        }
    }
}
