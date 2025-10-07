package javaproject;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.*;

public class LoginSignup {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginSignup().createAndShowGUI());
    }

    private JFrame frame;

    public void createAndShowGUI() {
        frame = new JFrame("Login and Signup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Login", createLoginPanel());
        tabbedPane.addTab("Signup", createSignupPanel());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // ------------------ LOGIN PANEL ------------------
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JButton loginButton = new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String email = emailField.getText();

            String role = validateLoginAndGetRole(username, password, email);
            if (role != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                frame.dispose();

                // Navigate based on role
                if ("Scheduler".equals(role)) {
                    SwingUtilities.invokeLater(() -> new SchedulerPage(username));
                } else if ("Driver".equals(role)) {
                    SwingUtilities.invokeLater(() -> new DriverHomePage(username));
                } else {
                    SwingUtilities.invokeLater(() -> new HomePage(username, role));
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username, password, or email.");
            }
        });

        return panel;
    }

    // ------------------ SIGNUP PANEL ------------------
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Choose Username:");
        JTextField userField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(20);

        JLabel passLabel = new JLabel("Choose Password:");
        JPasswordField passField = new JPasswordField(20);

        JLabel roleLabel = new JLabel("Select Role:");
        JRadioButton driverButton = new JRadioButton("Driver");
        JRadioButton schedulerButton = new JRadioButton("Scheduler");
        JRadioButton customerButton = new JRadioButton("Customer");

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(driverButton);
        roleGroup.add(schedulerButton);
        roleGroup.add(customerButton);

        JPanel rolePanel = new JPanel(new FlowLayout());
        rolePanel.add(driverButton);
        rolePanel.add(schedulerButton);
        rolePanel.add(customerButton);

        JLabel regNumLabel = new JLabel("Registration Number:");
        JTextField regNumField = new JTextField(20);
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField(20);

        regNumLabel.setVisible(false);
        regNumField.setVisible(false);
        capacityLabel.setVisible(false);
        capacityField.setVisible(false);

        driverButton.addActionListener(e -> {
            regNumLabel.setVisible(true);
            regNumField.setVisible(true);
            capacityLabel.setVisible(true);
            capacityField.setVisible(true);
        });

        schedulerButton.addActionListener(e -> {
            regNumLabel.setVisible(false);
            regNumField.setVisible(false);
            capacityLabel.setVisible(false);
            capacityField.setVisible(false);
        });

        customerButton.addActionListener(e -> {
            regNumLabel.setVisible(false);
            regNumField.setVisible(false);
            capacityLabel.setVisible(false);
            capacityField.setVisible(false);
        });

        JButton signupButton = new JButton("Signup");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(passLabel, gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(roleLabel, gbc);
        gbc.gridx = 1;
        panel.add(rolePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(regNumLabel, gbc);
        gbc.gridx = 1;
        panel.add(regNumField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(capacityLabel, gbc);
        gbc.gridx = 1;
        panel.add(capacityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(signupButton, gbc);

        signupButton.addActionListener(e -> {
            String username = userField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String password = new String(passField.getPassword());
            String role = getSelectedRole(driverButton, schedulerButton, customerButton);
            String registrationNumber = regNumField.getText();
            String capacity = capacityField.getText();

            if (validatePhoneNumber(phone) && validateSignup(username, email, phone, password)) {
                if (registerUser(username, password, email, phone, role, registrationNumber, capacity)) {
                    JOptionPane.showMessageDialog(frame, "Signup Successful!");

                    if (!"Driver".equals(role)) {
                        frame.dispose();
                        if ("Scheduler".equals(role)) {
                            SwingUtilities.invokeLater(() -> new SchedulerPage(username));
                        } else {
                            SwingUtilities.invokeLater(() -> new HomePage(username, role));
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Signup Failed. Username or email already exists.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please check your details.");
            }
        });

        return panel;
    }

    private String getSelectedRole(JRadioButton driverButton, JRadioButton schedulerButton, JRadioButton customerButton) {
        if (driverButton.isSelected()) return "Driver";
        if (schedulerButton.isSelected()) return "Scheduler";
        if (customerButton.isSelected()) return "Customer";
        return "";
    }

    private String validateLoginAndGetRole(String username, String password, String email) {
        String sql = "SELECT password, role FROM users WHERE username = ? AND email = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error validating login.");
        }
        return null;
    }

    private boolean validatePhoneNumber(String phone) {
        String regex = "^[0-9\\+\\-\\(\\)\\s]*$";
        return Pattern.matches(regex, phone);
    }

    private boolean validateSignup(String username, String email, String phone, String password) {
        String sql = "SELECT * FROM users WHERE email = ? OR phone = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, phone);
            ResultSet rs = stmt.executeQuery();
            return !rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error checking existing users.");
            return false;
        }
    }

    private boolean registerUser(String username, String password, String email, String phone, String role,
                                 String registrationNumber, String capacity) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password, email, phone, role, registration_number, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, role);
            stmt.setString(6, "Driver".equals(role) ? registrationNumber : null);
            stmt.setInt(7, "Driver".equals(role) ? Integer.parseInt(capacity) : 0);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving user to database.");
            return false;
        }
    }

    // ✅ Database helper
    static class DatabaseHelper {
        public static Connection getConnection() {
            try {
                String url = "jdbc:mysql://localhost:3306/userdb";
                String user = "root";
                String password = ""; // your MySQL password
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database connection failed!");
                return null;
            }
        }
    }
}
