package javaproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ScheduleOrderPage {
    private JFrame frame;
    private JTable table;
    private JComboBox<String> driverComboBox;
    private String selectedItem;  // Store the selected item

    public ScheduleOrderPage(String username) {
        frame = new JFrame("Schedule Order");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Label displaying the username
        JLabel label = new JLabel("Scheduling Order for: " + username, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));

        // Fetch customer data from the database
        Vector<Vector<Object>> data = fetchCustomerData(username);
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Order ID");
        columnNames.add("Full Name");
        columnNames.add("Address");
        columnNames.add("Phone");
        columnNames.add("Food Details");

        // Create table with data
        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // Create ComboBox for selecting a driver
        driverComboBox = new JComboBox<>(fetchDrivers());

        // If no data was fetched, show a message on the page
        if (data.isEmpty()) {
            JLabel noDataLabel = new JLabel("No records found for the username.", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(label);
            panel.add(Box.createVerticalStrut(20));
            panel.add(noDataLabel);
            panel.add(Box.createVerticalStrut(20));

            JButton backButton = new JButton("Back to Scheduler");
            backButton.addActionListener(e -> {
                frame.dispose();  // Close the current page
                SwingUtilities.invokeLater(() -> new SchedulerPage(username));  // Open the SchedulerPage
            });

            panel.add(backButton);
            frame.add(panel, BorderLayout.CENTER);
        } else {
            // Add data to the JTable and display
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(label);
            panel.add(Box.createVerticalStrut(20));
            panel.add(scrollPane);
            panel.add(Box.createVerticalStrut(20));

            // Add the driver ComboBox to the panel
            JLabel driverLabel = new JLabel("Select Driver: ");
            panel.add(driverLabel);
            panel.add(driverComboBox);

            panel.add(Box.createVerticalStrut(20));

            // Button to save the selected driver to the 'mission' database
            JButton saveButton = new JButton("Save Mission");
            saveButton.addActionListener(e -> saveMission(username));

            panel.add(saveButton);

            // Back button to go back to SchedulerPage
            JButton backButton = new JButton("Back to Scheduler");
            backButton.addActionListener(e -> {
                frame.dispose();  // Close the current page
                SwingUtilities.invokeLater(() -> new SchedulerPage(username));  // Open the SchedulerPage
            });

            panel.add(backButton);
            frame.add(panel, BorderLayout.CENTER);
        }

        frame.setVisible(true);
    }

    // Method to fetch customer data from the database
    private Vector<Vector<Object>> fetchCustomerData(String username) {
        Vector<Vector<Object>> data = new Vector<>();
        String query = "SELECT * FROM final_order WHERE username = ?";
        System.out.println("Fetching data for username: " + username);  // Debugging line

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            System.out.println("Executing query: " + stmt);  // Debugging line

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("order_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("address"));
                row.add(rs.getString("phone"));
                row.add(parseFoodDetails(rs.getString("food_details")));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Method to fetch list of drivers from the database
    private String[] fetchDrivers() {
        Vector<String> drivers = new Vector<>();
        String query = "SELECT username FROM users WHERE role = 'Driver'";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                drivers.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert Vector to String array
        return drivers.toArray(new String[0]);
    }

    // Method to parse food details from JSON string
    private String parseFoodDetails(String foodDetailsJson) {
        // Simple parsing example, assuming food_details is in a basic JSON format
        String formattedDetails = foodDetailsJson.replace("[", "").replace("]", "").replace("{", "").replace("}", "")
                .replace("\"food_name\":", "").replace("\"quantity\":", "Qty: ").replace("\"weight\":", "Weight: ");
        return formattedDetails;
    }

    // Method to save the selected driver and order details to the 'mission' database
    private void saveMission(String username) {
        String selectedDriver = (String) driverComboBox.getSelectedItem();

        // Get the selected row from the table
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {  // Check if a row is selected
            // Extract order details from the selected row
            int orderId = (int) table.getValueAt(selectedRow, 0);
            String fullName = (String) table.getValueAt(selectedRow, 1);
            String address = (String) table.getValueAt(selectedRow, 2);
            String phone = (String) table.getValueAt(selectedRow, 3);
            String foodDetails = (String) table.getValueAt(selectedRow, 4);

            // Prepare SQL query to insert all the details into the 'mission' table
            String query = "INSERT INTO mission (order_id, username, driver, full_name, address, phone, food_details) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mission", "root", "password");
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                // Set the values for the insert query
                stmt.setInt(1, orderId);
                stmt.setString(2, username);
                stmt.setString(3, selectedDriver);
                stmt.setString(4, fullName);
                stmt.setString(5, address);
                stmt.setString(6, phone);
                stmt.setString(7, foodDetails);

                // Execute the update
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Mission saved successfully!");

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving mission.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an order from the table.");
        }
    }

    public static void main(String[] args) {
        // Pass the username as an argument
        SwingUtilities.invokeLater(() -> new ScheduleOrderPage("R"));
        SwingUtilities.invokeLater(() -> new ScheduleOrderPage("Z"));
    }
    }

