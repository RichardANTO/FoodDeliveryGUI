package javaproject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditPage {
    private JFrame frame;
    private String username;
    private String role;
    private JTable foodTable;
    private JTextField nameField, addressField, phoneField;

    public EditPage(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Edit Order Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel with "Go Back" button
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new HomePage(username, role).createAndShowGUI(username, role));
        });
        topPanel.add(goBackButton, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Customer details panel
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel nameLabel = new JLabel("Full Name:");
        nameField = new JTextField(20);
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(20);
        JLabel phoneLabel = new JLabel("Phone:");
        phoneField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        customerPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        customerPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        customerPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        customerPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        customerPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        customerPanel.add(phoneField, gbc);

        mainPanel.add(customerPanel, BorderLayout.WEST);

        // Food table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Food Details"));

        String[] columns = {"Order ID", "Food Name", "Quantity", "Weight"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        foodTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(foodTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateOrderDetails());
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteOrderDetails());

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        loadOrderDetails();
    }

    private void loadOrderDetails() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "")) {
            // Load customer details
            String customerQuery = "SELECT full_name, address, phone FROM final_order WHERE username = ?";
            PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
            customerStmt.setString(1, username);
            ResultSet customerRs = customerStmt.executeQuery();
            if (customerRs.next()) {
                nameField.setText(customerRs.getString("full_name"));
                addressField.setText(customerRs.getString("address"));
                phoneField.setText(customerRs.getString("phone"));
            }

            // Load food details from the 'food_details' column
            String foodQuery = "SELECT order_id, food_details FROM final_order WHERE username = ?";
            PreparedStatement foodStmt = conn.prepareStatement(foodQuery);
            foodStmt.setString(1, username);
            ResultSet foodRs = foodStmt.executeQuery();

            DefaultTableModel tableModel = (DefaultTableModel) foodTable.getModel();
            tableModel.setRowCount(0); // Clear the table before adding rows

            while (foodRs.next()) {
                int orderId = foodRs.getInt("order_id");
                String foodDetails = foodRs.getString("food_details");

                // Assuming food_details is a JSON-like or comma-separated string, you'll need to parse it
                String[] details = foodDetails.split(","); // For comma-separated values: food_name, quantity, weight
                if (details.length == 3) {
                    String foodName = details[0].trim();
                    String quantity = details[1].trim();
                    String weight = details[2].trim();
                    tableModel.addRow(new Object[] { orderId, foodName, quantity, weight });
                }
            }

            // Add selection listener to the table
            foodTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && foodTable.getSelectedRow() >= 0) {
                    int selectedRow = foodTable.getSelectedRow();
                    int orderId = (int) tableModel.getValueAt(selectedRow, 0);
                    loadCustomerDetailsByOrderId(orderId);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomerDetailsByOrderId(int orderId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "")) {
            // Query to load customer details based on order_id
            String customerQuery = "SELECT full_name, address, phone FROM final_order WHERE order_id = ?";
            PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
            customerStmt.setInt(1, orderId);
            ResultSet customerRs = customerStmt.executeQuery();
            if (customerRs.next()) {
                // Set the customer details into the respective fields
                nameField.setText(customerRs.getString("full_name"));
                addressField.setText(customerRs.getString("address"));
                phoneField.setText(customerRs.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading customer details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrderDetails() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "")) {
            // Get the current time as a string in the format 'yyyy-MM-dd HH:mm:ss'
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String currentTime = LocalDateTime.now().format(formatter); // Get current timestamp as string

            // Update customer details
            String updateCustomerQuery = "UPDATE final_order SET full_name = ?, address = ?, phone = ?, update_time = ? WHERE username = ?";
            PreparedStatement customerStmt = conn.prepareStatement(updateCustomerQuery);
            customerStmt.setString(1, nameField.getText());
            customerStmt.setString(2, addressField.getText());
            customerStmt.setString(3, phoneField.getText());
            customerStmt.setString(4, currentTime); // Set the current timestamp
            customerStmt.setString(5, username);
            customerStmt.executeUpdate();

            // Update food details (assuming the data is saved in the same format)
            DefaultTableModel tableModel = (DefaultTableModel) foodTable.getModel();
            String updateFoodQuery = "UPDATE final_order SET food_details = ?, update_time = ? WHERE order_id = ? AND username = ?";
            PreparedStatement foodStmt = conn.prepareStatement(updateFoodQuery);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int orderId = (int) tableModel.getValueAt(i, 0);
                String foodName = (String) tableModel.getValueAt(i, 1);
                String quantity = (String) tableModel.getValueAt(i, 2);
                String weight = (String) tableModel.getValueAt(i, 3);
                String foodDetails = foodName + "," + quantity + "," + weight;

                foodStmt.setString(1, foodDetails);  // Set the updated food details
                foodStmt.setString(2, currentTime);  // Set the current timestamp for the update
                foodStmt.setInt(3, orderId);
                foodStmt.setString(4, username);
                foodStmt.addBatch();
            }
            foodStmt.executeBatch();

            JOptionPane.showMessageDialog(frame, "Order details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadOrderDetails();  // Refresh the data after update
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOrderDetails() {
        int selectedRow = foodTable.getSelectedRow(); // Get selected row from the table
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select an order to delete.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the order_id from the first column (assuming it's stored as an Integer in the table)
        Object orderIdObj = foodTable.getValueAt(selectedRow, 0);  // Get order_id as Object
        if (orderIdObj instanceof Integer) {
            int orderId = (Integer) orderIdObj;  // Safely cast to Integer
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this order?", "Delete Order", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "")) {
                    // Delete the order from the table
                    String deleteQuery = "DELETE FROM final_order WHERE order_id = ? AND username = ?";
                    PreparedStatement stmt = conn.prepareStatement(deleteQuery);
                    stmt.setInt(1, orderId);
                    stmt.setString(2, username);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "Order deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadOrderDetails(); // Refresh the data after deletion
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error deleting order.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error deleting order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditPage("sampleUser", "Customer").createAndShowGUI());
    }
}