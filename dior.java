package com.form;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.panel.*; 

public class dior extends JFrame {

	JTabbedPane tabs = new JTabbedPane();
	private int guestID; 
	private JButton logoutBtn; 

	public dior(String role, int userId) {
		this.guestID = userId;

		setTitle("Hospital Portal System");
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		logoutBtn = new JButton("Logout");
		logoutBtn.setForeground(Color.WHITE);
		logoutBtn.setBackground(Color.RED);
		logoutBtn.setFocusPainted(false);
		logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));

		logoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(
						dior.this,
						"Are you sure you want to logout?",
						"Logout Confirmation",
						JOptionPane.YES_NO_OPTION
						);

				if (confirm == JOptionPane.YES_OPTION) {
					dispose(); 
					new HospitalPortalLogin(); 
				}
			}
		});


		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topPanel.add(logoutBtn);


		if (role.equalsIgnoreCase("Admin")) {
			tabs.add("Guest", new GuestPanel());
			tabs.add("Invoice", new InvoicePanel());
			tabs.add("Invoice Service", new InvoiceServicePanel());
			tabs.add("Reservation", new ReservationPanel());
			tabs.add("Room", new RoomPanel());
			tabs.add("Room Service", new RoomServicePanel());
			tabs.add("Service", new ServicePanel());
			tabs.add("Staff", new StaffPanel());
		}

		else if (role.equalsIgnoreCase("Guest")) {
			tabs.add("Guest", new Guest());
			tabs.add("Reservation", new ReservationPanel());
			tabs.add("Room", new RoommPanel());
			tabs.add("Service", new ServisePanel());
			tabs.add("Invoices", new InvoiccePanel());
		}

		else if (role.equalsIgnoreCase("Staff")) {
			tabs.add("Guest", new GuestPanel());
			tabs.add("Invoice", new InvoicePanel());
			tabs.add("Invoice Service", new InvoiceServicePanel());
			tabs.add("Reservation", new ReservationPanel());
			tabs.add("Room", new RoomPanel());
			tabs.add("Room Service", new RoomServicePanel());
			tabs.add("Service", new ServicePanel());
		}


		add(topPanel, BorderLayout.NORTH);
		add(tabs, BorderLayout.CENTER);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}


	private Component createTablePanel(String query) {

		return new JLabel("Data from: " + query);
	}
}
