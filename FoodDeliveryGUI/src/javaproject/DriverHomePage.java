package javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class DriverHomePage {
    private JFrame frame;
    private JTable table;
    private String username;

    public DriverHomePage(String username) {
        this.username = username;
        
        frame = new JFrame("Driver Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        Vector<Vector<Object>> data = fetchMissionData(username);
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Order ID");
        columnNames.add("Food Item");
        columnNames.add("Driver");
        columnNames.add("Full Name");
        columnNames.add("Address");
        columnNames.add("Phone");

        table = new JTable(data, columnNames);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton deliveredButton = new JButton("Successfully Delivered");
        deliveredButton.addActionListener(this::markOrderAsDelivered);
        buttonPanel.add(deliveredButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
    }

    private Vector<Vector<Object>> fetchMissionData(String username) {
        Vector<Vector<Object>> data = new Vector<>();
        String query = "SELECT * FROM scheduled_orders WHERE driver_username = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_scheduler_db", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("order_id"));
                row.add(rs.getString("food_details"));
                row.add(rs.getString("driver_username"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("address"));
                row.add(rs.getString("phone"));
                data.add(row);
            }

            // If no data is found, show a message
            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No mission for you.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }


    private void markOrderAsDelivered(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (int) table.getValueAt(selectedRow, 0);

            String updateQuery = "UPDATE scheduled_orders SET status = 'Delivered' WHERE order_id = ?";
            String deleteScheduledQuery = "DELETE FROM scheduled_orders WHERE order_id = ?";
            String deleteFinalQuery = "DELETE FROM final_order WHERE order_id = ?";

            try (Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_scheduler_db", "root", "password");
                 PreparedStatement updateStmt = conn1.prepareStatement(updateQuery);
                 PreparedStatement deleteScheduledStmt = conn1.prepareStatement(deleteScheduledQuery);
                 Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "password");
                 PreparedStatement deleteFinalStmt = conn2.prepareStatement(deleteFinalQuery)) {

                // Update scheduled_orders status
                updateStmt.setInt(1, orderId);
                int updatedRows = updateStmt.executeUpdate();

                if (updatedRows > 0) {
                    // Delete from scheduled_orders
                    deleteScheduledStmt.setInt(1, orderId);
                    int deletedScheduledRows = deleteScheduledStmt.executeUpdate();

                    // Delete from final_order
                    deleteFinalStmt.setInt(1, orderId);
                    int deletedFinalRows = deleteFinalStmt.executeUpdate();

                    if (deletedScheduledRows > 0 && deletedFinalRows > 0) {
                        JOptionPane.showMessageDialog(frame, "Order ID " + orderId + " marked as Delivered and removed from records Great Job.");
                        frame.dispose();
                        new DriverHomePage(username); // Refresh page
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to remove Order ID " + orderId + " from records.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Error updating order status.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database error while updating order status.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an order to mark as delivered.");
        }
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            frame.dispose();
            new LoginSignup();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DriverHomePage("DriverName"));
    }
}
