package com.panel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DB;

public class ReservationPanel extends JPanel {
	private JTable table;
	private DefaultTableModel model;

	public ReservationPanel() {
		setLayout(new BorderLayout(10, 10));

		JLabel title = new JLabel("Reservation Panel", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 22));
		add(title, BorderLayout.NORTH);

		String[] columns = {"ReservationID", "GuestID", "OrderNumber", "Date", "Status", "TotalAmount", "PaymentMethod", "Notes"};
		model = new DefaultTableModel(columns, 0);
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		JButton addBtn = new JButton("Add Reservation");
		JButton updateBtn = new JButton("Update Reservation");
		JButton cancelBtn = new JButton("Cancel Reservation");
		JButton refreshBtn = new JButton("Refresh");

		buttons.add(addBtn);
		buttons.add(updateBtn);
		buttons.add(cancelBtn);
		buttons.add(refreshBtn);
		add(buttons, BorderLayout.SOUTH);

		loadReservationData();

		refreshBtn.addActionListener(e -> loadReservationData());
		addBtn.addActionListener(e -> addReservation());
		updateBtn.addActionListener(e -> updateReservation());
		cancelBtn.addActionListener(e -> cancelReservation());
	}

	private void loadReservationData() {
		model.setRowCount(0);
		try (Connection con = DB.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM reservation")) {

			while (rs.next()) {
				model.addRow(new Object[]{
						rs.getInt("ReservationID"),
						rs.getInt("GuestID"),
						rs.getString("OrderNumber"),
						rs.getDate("Date"),
						rs.getString("Status"),
						rs.getDouble("TotalAmount"),
						rs.getString("PaymentMethod"),
						rs.getString("Notes")
				});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error loading reservations: " + e.getMessage());
		}
	}

	private void addReservation() {
		JTextField guestID = new JTextField();
		JTextField orderNum = new JTextField();
		JTextField dateField = new JTextField();
		JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending","Confirmed","Cancelled","Completed"});
		JTextField totalAmount = new JTextField();
		JComboBox<String> paymentBox = new JComboBox<>(new String[]{"Cash","Card","MOMOPay"});
		JTextField notesField = new JTextField();

		Object[] inputs = {
				"GuestID:", guestID,
				"OrderNumber:", orderNum,
				"Date (yyyy-MM-dd):", dateField,
				"Status:", statusBox,
				"Total Amount:", totalAmount,
				"Payment Method:", paymentBox,
				"Notes:", notesField
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Add Reservation", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement(
							"INSERT INTO reservation (GuestID, OrderNumber, Date, Status, TotalAmount, PaymentMethod, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

				ps.setInt(1, Integer.parseInt(guestID.getText()));
				ps.setString(2, orderNum.getText());
				ps.setDate(3, java.sql.Date.valueOf(dateField.getText()));
				ps.setString(4, statusBox.getSelectedItem().toString());
				ps.setDouble(5, Double.parseDouble(totalAmount.getText()));
				ps.setString(6, paymentBox.getSelectedItem().toString());
				ps.setString(7, notesField.getText());

				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Reservation added successfully!");
				loadReservationData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error adding reservation: " + e.getMessage());
			}
		}
	}

	private void updateReservation() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Select a reservation to update");
			return;
		}

		int resID = (int) model.getValueAt(selectedRow, 0);

		JTextField guestID = new JTextField(String.valueOf(model.getValueAt(selectedRow, 1)));
		JTextField orderNum = new JTextField(String.valueOf(model.getValueAt(selectedRow, 2)));
		JTextField dateField = new JTextField(model.getValueAt(selectedRow, 3).toString());
		JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending","Confirmed","Cancelled","Completed"});
		statusBox.setSelectedItem(model.getValueAt(selectedRow, 4).toString());
		JTextField totalAmount = new JTextField(String.valueOf(model.getValueAt(selectedRow, 5)));
		JComboBox<String> paymentBox = new JComboBox<>(new String[]{"Cash","Card","MOMOPay"});
		paymentBox.setSelectedItem(model.getValueAt(selectedRow, 6).toString());
		JTextField notesField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 7)));

		Object[] inputs = {
				"GuestID:", guestID,
				"OrderNumber:", orderNum,
				"Date (yyyy-MM-dd):", dateField,
				"Status:", statusBox,
				"Total Amount:", totalAmount,
				"Payment Method:", paymentBox,
				"Notes:", notesField
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Update Reservation", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement(
							"UPDATE reservation SET GuestID=?, OrderNumber=?, Date=?, Status=?, TotalAmount=?, PaymentMethod=?, Notes=? WHERE ReservationID=?")) {

				ps.setInt(1, Integer.parseInt(guestID.getText()));
				ps.setString(2, orderNum.getText());
				ps.setDate(3, java.sql.Date.valueOf(dateField.getText()));
				ps.setString(4, statusBox.getSelectedItem().toString());
				ps.setDouble(5, Double.parseDouble(totalAmount.getText()));
				ps.setString(6, paymentBox.getSelectedItem().toString());
				ps.setString(7, notesField.getText());
				ps.setInt(8, resID);

				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Reservation updated successfully!");
				loadReservationData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error updating reservation: " + e.getMessage());
			}
		}
	}

	private void cancelReservation() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Select a reservation to cancel");
			return;
		}

		int resID = (int) model.getValueAt(selectedRow, 0);

		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this reservation?", "Cancel Reservation", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement(
							"UPDATE reservation SET Status='Cancelled' WHERE ReservationID=?")) {

				ps.setInt(1, resID);
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
				loadReservationData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error cancelling reservation: " + e.getMessage());
			}
		}
	}
}
