package javaproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewFinalOrderDataGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewFinalOrderDataGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("View Final Order Data");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // Create a panel to hold the table
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and JTable
        String[] columnNames = {"Order ID", "Username", "Full Name", "Address", "Phone", "Food Details", "Order Date", "Update Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        // Scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load data from the database
        loadData(tableModel);

        // Add panel to the frame
        frame.add(panel);
        
        // Make the frame visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadData(DefaultTableModel tableModel) {
        // Database credentials
        String url = "jdbc:mysql://localhost:3306/userdb"; // Replace 'localhost' and 'userdb' with your server and database name
        String user = "root"; // Replace with your MySQL username
        String password = "your_password"; // Replace with your MySQL password

        // SQL query to fetch all data from the final_order table
        String query = "SELECT * FROM final_order";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate through the result set and add rows to the table model
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
            JOptionPane.showMessageDialog(null, "Error fetching data from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
