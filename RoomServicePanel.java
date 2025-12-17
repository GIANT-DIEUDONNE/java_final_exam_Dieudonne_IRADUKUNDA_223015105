package com.panel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DB;
public class RoomServicePanel extends JPanel {
	private JTable table;
	private DefaultTableModel model;

	public RoomServicePanel() {
		setLayout(new BorderLayout(10, 10));

		JLabel title = new JLabel("Room Services Panel", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 22));
		add(title, BorderLayout.NORTH);

		String[] columns = {"Room Name", "Service Name"};
		model = new DefaultTableModel(columns, 0);
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		JButton assignBtn = new JButton("Assign Service");
		JButton updateBtn = new JButton("Update");
		JButton deleteBtn = new JButton("Delete");
		JButton refreshBtn = new JButton("Refresh");

		buttons.add(assignBtn);
		buttons.add(updateBtn);
		buttons.add(deleteBtn);
		buttons.add(refreshBtn);
		add(buttons, BorderLayout.SOUTH);

		loadRoomServiceData();

		refreshBtn.addActionListener(e -> loadRoomServiceData());
		assignBtn.addActionListener(e -> assignService());
		updateBtn.addActionListener(e -> updateService());
		deleteBtn.addActionListener(e -> deleteService());
	}

	private void loadRoomServiceData() {
		model.setRowCount(0);
		String query = """
				    SELECT 
				        r.Name AS RoomName,
				        s.Name AS ServiceName
				    FROM roomservice rs
				    JOIN room r ON rs.RoomID = r.RoomID
				    JOIN service s ON rs.ServiceID = s.ServiceID
				""";

		try (Connection con = DB.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(query)) {

			while (rs.next()) {
				model.addRow(new Object[]{
						rs.getString("RoomName"),
						rs.getString("ServiceName")
				});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error loading room services: " + e.getMessage());
		}
	}

	private void assignService() {
		JTextField roomField = new JTextField();
		JTextField serviceField = new JTextField();

		Object[] inputs = {
				"Room ID:", roomField,
				"Service ID:", serviceField
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Assign Room Service", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement("INSERT INTO roomservice (RoomID, ServiceID) VALUES (?, ?)")) {

				ps.setInt(1, Integer.parseInt(roomField.getText()));
				ps.setInt(2, Integer.parseInt(serviceField.getText()));

				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Service assigned to room successfully!");
				loadRoomServiceData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error assigning service: " + e.getMessage());
			}
		}
	}

	private void updateService() {
		JOptionPane.showMessageDialog(this,
				"Update currently works using RoomID and ServiceID.\nTo change names, update the respective tables.");
	}

	private void deleteService() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Select a room service to delete");
			return;
		}

		String roomName = (String) model.getValueAt(selectedRow, 0);
		String serviceName = (String) model.getValueAt(selectedRow, 1);

		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete service '" + serviceName +
				"' from room '" + roomName + "'?",
				"Delete Room Service",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			try (Connection con = DB.getConnection();
					PreparedStatement ps = con.prepareStatement("""
							    DELETE rs FROM roomservice rs
							    JOIN room r ON rs.RoomID = r.RoomID
							    JOIN service s ON rs.ServiceID = s.ServiceID
							    WHERE r.Name = ? AND s.Name = ?
							""")) {

				ps.setString(1, roomName);
				ps.setString(2, serviceName);
				ps.executeUpdate();

				JOptionPane.showMessageDialog(this, "Room service deleted successfully!");
				loadRoomServiceData();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error deleting room service: " + e.getMessage());
			}
		}
	}
}
