package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DB;

public class InvoiceServicePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public InvoiceServicePanel() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Invoice Services Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Invoice ID", "Service Name"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadInvoiceServiceData());
        addBtn.addActionListener(e -> addInvoiceService());
        deleteBtn.addActionListener(e -> deleteInvoiceService());

        loadInvoiceServiceData();
    }

    private void loadInvoiceServiceData() {
        model.setRowCount(0);
        String query = """
            SELECT i.InvoiceID, s.Name AS ServiceName
            FROM invoiceservice isv
            JOIN invoice i ON isv.InvoiceID = i.InvoiceID
            JOIN service s ON isv.ServiceID = s.ServiceID
        """;

        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("InvoiceID"),
                        rs.getString("ServiceName")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addInvoiceService() {
        JTextField invoiceField = new JTextField();
        JTextField serviceField = new JTextField();

        Object[] inputs = {
                "Invoice ID:", invoiceField,
                "Service ID:", serviceField
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Add Invoice Service", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO invoiceservice (InvoiceID, ServiceID) VALUES (?, ?)")) {

                ps.setInt(1, Integer.parseInt(invoiceField.getText()));
                ps.setInt(2, Integer.parseInt(serviceField.getText()));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Invoice service added successfully!");
                loadInvoiceServiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding invoice service: " + e.getMessage());
            }
        }
    }

    private void deleteInvoiceService() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete");
            return;
        }

        int invoiceID = (int) model.getValueAt(selectedRow, 0);
        String serviceName = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete service '" + serviceName + "' from invoice " + invoiceID + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("""
                     DELETE isv FROM invoiceservice isv
                     JOIN service s ON isv.ServiceID = s.ServiceID
                     WHERE isv.InvoiceID = ? AND s.Name = ?
                 """)) {

                ps.setInt(1, invoiceID);
                ps.setString(2, serviceName);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Deleted successfully!");
                loadInvoiceServiceData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Invoice Service Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 400);
            frame.setLocationRelativeTo(null);
            frame.add(new InvoiceServicePanel());
            frame.setVisible(true);
        });
    }
}
