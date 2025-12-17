package com.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.prefs.Preferences;
import com.util.DB;

public class HospitalPortalLogin extends JFrame implements ActionListener {
	private JTextField usertxt = new JTextField(15);
	private JPasswordField passtxt = new JPasswordField(15);
	private JButton loginbtn = new JButton("LOGIN");
	private JButton cancelbtn = new JButton("CANCEL");
	private JButton signupbtn = new JButton("SIGN UP");
	private JCheckBox notRobotChk = new JCheckBox("I'm not a robot");
	private JCheckBox showPassChk = new JCheckBox("Show Password");
	private JCheckBox rememberMeChk = new JCheckBox("Remember Me");
	private JButton forgotPassBtn = new JButton("Forgot Password?");
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

	public HospitalPortalLogin() {
		setTitle("Login Form");
		setSize(500, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUndecorated(false);

		JPanel container = new JPanel(new BorderLayout());
		container.setBackground(new Color(240, 242, 245));

		// Header
		JPanel header = new JPanel();
		header.setBackground(new Color(30, 144, 255));
		JLabel title = new JLabel("WELCOME TO HOSPITAL PORTAL SYSTEM");
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		header.add(title);

		// Main Panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(20, 30, 20, 30)
				));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Username
		gbc.gridx = 0; gbc.gridy = 0; mainPanel.add(new JLabel("Username:"), gbc);
		gbc.gridx = 1; styleTextField(usertxt); mainPanel.add(usertxt, gbc);

		// Password
		gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(new JLabel("Password:"), gbc);
		gbc.gridx = 1; styleTextField(passtxt); mainPanel.add(passtxt, gbc);

		// Show Password
		gbc.gridx = 1; gbc.gridy = 2; mainPanel.add(showPassChk, gbc);

		// Remember Me
		gbc.gridx = 1; gbc.gridy = 3; mainPanel.add(rememberMeChk, gbc);

		// Not Robot
		gbc.gridx = 1; gbc.gridy = 4; mainPanel.add(notRobotChk, gbc);

		// Forgot Password
		forgotPassBtn.setForeground(new Color(30, 144, 255));
		forgotPassBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		forgotPassBtn.setBorderPainted(false);
		forgotPassBtn.setContentAreaFilled(false);
		gbc.gridx = 1; gbc.gridy = 5; mainPanel.add(forgotPassBtn, gbc);

		// Sign Up
		signupbtn.setForeground(new Color(30, 144, 255));
		signupbtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		signupbtn.setBorderPainted(false);
		signupbtn.setContentAreaFilled(false);
		gbc.gridx = 1; gbc.gridy = 6; mainPanel.add(signupbtn, gbc);

		// Buttons
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
		styleButton(loginbtn, new Color(46, 204, 113));
		styleButton(cancelbtn, new Color(231, 76, 60));
		buttonPanel.add(loginbtn);
		buttonPanel.add(cancelbtn);
		gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
		mainPanel.add(buttonPanel, gbc);

		container.add(header, BorderLayout.NORTH);
		container.add(mainPanel, BorderLayout.CENTER);
		add(container);

		// Listeners
		loginbtn.addActionListener(this);
		cancelbtn.addActionListener(this);
		signupbtn.addActionListener(this);
		forgotPassBtn.addActionListener(this);
		showPassChk.addActionListener(e -> passtxt.setEchoChar(showPassChk.isSelected() ? (char)0 : '•'));

		// Load saved username
		String savedUser = prefs.get("username", "");
		if (!savedUser.isEmpty()) {
			usertxt.setText(savedUser);
			rememberMeChk.setSelected(true);
		}

		setVisible(true);
	}

	private void styleButton(JButton btn, Color bgColor) {
		btn.setFocusPainted(false);
		btn.setBackground(bgColor);
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btn.setPreferredSize(new Dimension(100, 35));
		btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bgColor.darker()); }
			public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bgColor); }
		});
	}

	private void styleTextField(JTextField txt) {
		txt.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)
				));
		txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	}

	private String hashPassword(String password) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(password.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte b : hash) sb.append(String.format("%02x", b));
		return sb.toString();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelbtn) System.exit(0);

		if (e.getSource() == signupbtn) new SignUpForm();

		if (e.getSource() == forgotPassBtn) {
			String username = JOptionPane.showInputDialog(this, "Enter your username:");
			if (username != null && !username.isEmpty()) {
				try (Connection con = DB.getConnection()) {
					PreparedStatement ps = con.prepareStatement("SELECT Email FROM guest WHERE Username=?");
					ps.setString(1, username);
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						JOptionPane.showMessageDialog(this,
								"Password recovery link sent to: " + rs.getString("Email") + "\n(Email sending not implemented)",
								"Forgot Password", JOptionPane.INFORMATION_MESSAGE);
					} else JOptionPane.showMessageDialog(this, "Username not found!");
				} catch (Exception ex) { ex.printStackTrace(); }
			}
		}

		if (e.getSource() == loginbtn) {
			if (!notRobotChk.isSelected()) {
				JOptionPane.showMessageDialog(this, "Please confirm you are not a robot!");
				return;
			}

			try (Connection con = DB.getConnection()) {
				String sql = "SELECT * FROM guest WHERE Username=? AND PasswordHash=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, usertxt.getText());
				ps.setString(2, hashPassword(new String(passtxt.getPassword())));
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					String role = rs.getString("Role");
					int guestId = rs.getInt("GuestID");
					String fullName = rs.getString("FullName");

					// Update LastLogin
					PreparedStatement psUpdate = con.prepareStatement("UPDATE guest SET LastLogin = NOW() WHERE GuestID = ?");
					psUpdate.setInt(1, guestId); psUpdate.executeUpdate();

					if (rememberMeChk.isSelected()) prefs.put("username", usertxt.getText());
					else prefs.remove("username");

					JOptionPane.showMessageDialog(this, "Welcome " + fullName + "!\nRole: " + role, "Login Successful", JOptionPane.INFORMATION_MESSAGE);
					dispose();
					new dior(role, guestId); // Launch main portal
				} else JOptionPane.showMessageDialog(this, "Invalid username or password");
			} catch (Exception ex) { ex.printStackTrace(); }
		}
	}

	public static void main(String[] args) { SwingUtilities.invokeLater(HospitalPortalLogin::new); }
}

class SignUpForm extends JFrame implements ActionListener {
	private JTextField usernameField = new JTextField(15);
	private JPasswordField passField = new JPasswordField(15);
	private JTextField fullNameField = new JTextField(15);
	private JTextField emailField = new JTextField(15);
	private JComboBox<String> roleBox = new JComboBox<>(new String[]{"Guest", "Staff", "Admin"});
	private JButton signupBtn = new JButton("SIGN UP");
	private JButton cancelBtn = new JButton("CANCEL");

	public SignUpForm() {
		setTitle("Sign Up Form");
		setSize(400, 400);
		setLocationRelativeTo(null);
		setLayout(new GridBagLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Full Name:"), gbc);
		gbc.gridx = 1; add(fullNameField, gbc);

		gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Email:"), gbc);
		gbc.gridx = 1; add(emailField, gbc);

		gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Username:"), gbc);
		gbc.gridx = 1; add(usernameField, gbc);

		gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Password:"), gbc);
		gbc.gridx = 1; add(passField, gbc);

		gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Role:"), gbc);
		gbc.gridx = 1; add(roleBox, gbc);

		JPanel btnPanel = new JPanel();
		btnPanel.add(signupBtn);
		btnPanel.add(cancelBtn);
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; add(btnPanel, gbc);

		signupBtn.addActionListener(this);
		cancelBtn.addActionListener(e -> dispose());

		setVisible(true);
	}

	private String hashPassword(String password) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(password.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte b : hash) sb.append(String.format("%02x", b));
		return sb.toString();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == signupBtn) {
			String username = usernameField.getText();
			String password = new String(passField.getPassword());
			String fullName = fullNameField.getText();
			String email = emailField.getText();
			String role = (String) roleBox.getSelectedItem();

			if(username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please fill all fields!"); return;
			}

			try (Connection con = DB.getConnection()) {
				String sql = "INSERT INTO guest (Username, PasswordHash, FullName, Email, Role, CreatedAt) VALUES (?, ?, ?, ?, ?, NOW())";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, hashPassword(password));
				ps.setString(3, fullName);
				ps.setString(4, email);
				ps.setString(5, role);
				ps.executeUpdate();

				JOptionPane.showMessageDialog(this, "Account created successfully!"); dispose();
			} catch (SQLIntegrityConstraintViolationException ex) {
				JOptionPane.showMessageDialog(this, "Username or email already exists!");
			} catch (Exception ex) {
				ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
			}
		}
	}
}
