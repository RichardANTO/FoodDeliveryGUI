package javaproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SchedulerPage {
    private JFrame frame;

    public SchedulerPage(String username) {
        // Initialize the frame for the Scheduler page
        frame = new JFrame("Scheduler Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600); // Increased width to fit two tables
        frame.setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Welcome label at the top
        JLabel welcomeLabel = new JLabel("Welcome, Scheduler: " + username, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Split panel to hold the two tables side by side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6); // Allocate more space to the Customer Details table

        // Customer Details table
        JPanel customerPanel = new JPanel(new BorderLayout());
        JLabel customerTableTitle = new JLabel("Customer Details", SwingConstants.CENTER);
        customerTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        String[] customerColumnNames = {
            "Order ID", "Username", "Full Name", "Address", "Phone",
            "Food Details", "Order Date", "Update Time"
        };
        DefaultTableModel customerTableModel = new DefaultTableModel(customerColumnNames, 0);
        JTable customerTable = new JTable(customerTableModel);
        customerTable.setFillsViewportHeight(true);
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerPanel.add(customerTableTitle, BorderLayout.NORTH);
        customerPanel.add(customerScrollPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(customerPanel);

        // List of Drivers Available table
        JPanel driverPanel = new JPanel(new BorderLayout());
        JLabel driverTableTitle = new JLabel("List of Drivers Available", SwingConstants.CENTER);
        driverTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        String[] driverColumnNames = {
            "Driver Username", "Phone", "Capacity"
        };
        DefaultTableModel driverTableModel = new DefaultTableModel(driverColumnNames, 0);
        JTable driverTable = new JTable(driverTableModel);
        driverTable.setFillsViewportHeight(true);
        JScrollPane driverScrollPane = new JScrollPane(driverTable);
        driverPanel.add(driverTableTitle, BorderLayout.NORTH);
        driverPanel.add(driverScrollPane, BorderLayout.CENTER);
        splitPane.setRightComponent(driverPanel);

        // Add the split pane to the center of the main panel
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Load data into the tables
        loadCustomerData(customerTableModel);
        loadDriverData(driverTableModel);

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center-align buttons with spacing

        JButton scheduleOrderButton = new JButton("Schedule Order");
        scheduleOrderButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new ScheduleOrderPage(username));
        });

        JButton placeDriverButton = new JButton("Place Driver");
        placeDriverButton.addActionListener(e -> {
            placeDriverData(customerTable, driverTable);
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new LoginSignup().createAndShowGUI());
        });

        // Add buttons to the panel
        buttonPanel.add(scheduleOrderButton);
        buttonPanel.add(placeDriverButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame and display
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void loadCustomerData(DefaultTableModel tableModel) {
        String url = "jdbc:mysql://localhost:3306/userdb";
        String user = "root";
        String password = "your_password";

        String query = "SELECT * FROM final_order";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String foodDetails = rs.getString("food_details");
                String orderDate = rs.getString("order_date");
                String updateTime = rs.getString("update_time");

                Object[] row = {orderId, username, fullName, address, phone, foodDetails, orderDate, updateTime};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching customer data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDriverData(DefaultTableModel tableModel) {
        String url = "jdbc:mysql://localhost:3306/userdb";
        String user = "root";
        String password = "your_password";

        String query = "SELECT username, phone, capacity FROM users WHERE role = 'Driver'";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String driverUsername = rs.getString("username");
                String phone = rs.getString("phone");
                String capacity = rs.getString("capacity");

                Object[] row = {driverUsername, phone, capacity};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching driver data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void placeDriverData(JTable customerTable, JTable driverTable) {
        int selectedCustomerRow = customerTable.getSelectedRow();
        int selectedDriverRow = driverTable.getSelectedRow();

        if (selectedCustomerRow == -1 || selectedDriverRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a customer and a driver.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int orderId = (int) customerTable.getValueAt(selectedCustomerRow, 0);
        String username = (String) customerTable.getValueAt(selectedCustomerRow, 1);
        String fullName = (String) customerTable.getValueAt(selectedCustomerRow, 2);
        String address = (String) customerTable.getValueAt(selectedCustomerRow, 3);
        String phone = (String) customerTable.getValueAt(selectedCustomerRow, 4);
        String foodDetails = (String) customerTable.getValueAt(selectedCustomerRow, 5);
        String orderDate = (String) customerTable.getValueAt(selectedCustomerRow, 6);

        String driverUsername = (String) driverTable.getValueAt(selectedDriverRow, 0);
        String driverPhone = (String) driverTable.getValueAt(selectedDriverRow, 1);
        String driverCapacity = (String) driverTable.getValueAt(selectedDriverRow, 2);

        String dbUrl = "jdbc:mysql://localhost:3306/order_scheduler_db"; // New database
        String dbUser = "root";
        String dbPassword = "your_password";
        String insertQuery = "INSERT INTO scheduled_orders (order_id, username, full_name, address, phone, food_details, order_date, " +
                "driver_username, driver_phone, driver_capacity, scheduled_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, orderId);
            stmt.setString(2, username);
            stmt.setString(3, fullName);
            stmt.setString(4, address);
            stmt.setString(5, phone);
            stmt.setString(6, foodDetails);
            stmt.setString(7, orderDate);
            stmt.setString(8, driverUsername);
            stmt.setString(9, driverPhone);
            stmt.setString(10, driverCapacity);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Driver has been successfully scheduled.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error scheduling driver: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
