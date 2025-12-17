package com.panel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.util.DB; 

public class GuestPanel extends JPanel {
	private JTable table;
	private DefaultTableModel model;

	public GuestPanel() {
		setLayout(new BorderLayout(10, 10));
		JLabel title = new JLabel("Guest Panel", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 22));
		add(title, BorderLayout.NORTH);

		String[] columns = {"GuestID", "Username", "FullName", "Email", "Role", "CreatedAt", "LastLogin"};
		model = new DefaultTableModel(columns, 0);
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		JButton addBtn = new JButton("Add Guest");
		JButton updateBtn = new JButton("Update Guest");
		JButton deleteBtn = new JButton("Delete Guest");
		JButton refreshBtn = new JButton("Refresh");

		buttons.add(addBtn);
		buttons.add(updateBtn);
		buttons.add(deleteBtn);
		buttons.add(refreshBtn);
		add(buttons, BorderLayout.SOUTH);

		loadGuestData();

		refreshBtn.addActionListener(e -> loadGuestData());

		addBtn.addActionListener(e -> addGuest());
		updateBtn.addActionListener(e -> updateGuest());
		deleteBtn.addActionListener(e -> deleteGuest());
	}

	private void loadGuestData() {
		model.setRowCount(0);
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

	private void addGuest() {
		JTextField usernameField = new JTextField();
		JTextField fullnameField = new JTextField();
		JTextField emailField = new JTextField();
		JComboBox<String> roleBox = new JComboBox<>(new String[]{"Guest","Admin","Staff"});
		JPasswordField passwordField = new JPasswordField();

		Object[] inputs = {
				"Username:", usernameField,
				"Password:", passwordField,
				"Full Name:", fullnameField,
				"Email:", emailField,
				"Role:", roleBox
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Add Guest", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement(
							"INSERT INTO guest (Username, PasswordHash, FullName, Email, Role) VALUES (?, ?, ?, ?, ?)")) {

				ps.setString(1, usernameField.getText());
				ps.setString(2, hashPassword(new String(passwordField.getPassword())));
				ps.setString(3, fullnameField.getText());
				ps.setString(4, emailField.getText());
				ps.setString(5, roleBox.getSelectedItem().toString());

				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Guest added successfully!");
				loadGuestData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error adding guest: " + e.getMessage());
			}
		}
	}


	private void updateGuest() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Select a guest to update");
			return;
		}

		int guestID = (int) model.getValueAt(selectedRow, 0);
		String currentUsername = (String) model.getValueAt(selectedRow, 1);
		String currentFullName = (String) model.getValueAt(selectedRow, 2);
		String currentEmail = (String) model.getValueAt(selectedRow, 3);
		String currentRole = (String) model.getValueAt(selectedRow, 4);

		JTextField usernameField = new JTextField(currentUsername);
		JTextField fullnameField = new JTextField(currentFullName);
		JTextField emailField = new JTextField(currentEmail);
		JComboBox<String> roleBox = new JComboBox<>(new String[]{"Guest","Admin","Staff"});
		roleBox.setSelectedItem(currentRole);

		Object[] inputs = {
				"Username:", usernameField,
				"Full Name:", fullnameField,
				"Email:", emailField,
				"Role:", roleBox
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Update Guest", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement(
							"UPDATE guest SET Username=?, FullName=?, Email=?, Role=? WHERE GuestID=?")) {

				ps.setString(1, usernameField.getText());
				ps.setString(2, fullnameField.getText());
				ps.setString(3, emailField.getText());
				ps.setString(4, roleBox.getSelectedItem().toString());
				ps.setInt(5, guestID);

				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Guest updated successfully!");
				loadGuestData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error updating guest: " + e.getMessage());
			}
		}
	}

	private void deleteGuest() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Select a guest to delete");
			return;
		}

		int guestID = (int) model.getValueAt(selectedRow, 0);
		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this guest?", "Delete Guest", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement("DELETE FROM guest WHERE GuestID=?")) {
				ps.setInt(1, guestID);
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Guest deleted successfully!");
				loadGuestData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error deleting guest: " + e.getMessage());
			}
		}
	}


	private String hashPassword(String password) throws Exception {
		java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(password.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte b : hash) sb.append(String.format("%02x", b));
		return sb.toString();
	}
}
