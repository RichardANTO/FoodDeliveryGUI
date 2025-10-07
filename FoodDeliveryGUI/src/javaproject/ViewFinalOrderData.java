package javaproject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewFinalOrderData {
    public static void main(String[] args) {
        // Database credentials
        String url = "jdbc:mysql://localhost:3306/userdb"; // Replace 'localhost' and 'db' with your server and database name
        String user = "root"; // Replace with your MySQL username
        String password = "your_password"; // Replace with your MySQL password

        // SQL query to fetch all data from the final_order table
        String query = "SELECT * FROM final_order";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Print column headers
            System.out.printf("%-10s %-15s %-20s %-30s %-15s %-30s %-20s %-20s%n",
                "Order ID", "Username", "Full Name", "Address", "Phone", "Food Details", "Order Date", "Update Time");

            System.out.println("=".repeat(150));


            // Iterate through the result set and print the data
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String foodDetails = rs.getString("food_details");
                String orderDate = rs.getString("order_date");
                String updateTime = rs.getString("update_time");

                System.out.printf("%-10d %-15s %-20s %-30s %-15s %-30s %-20s %-20s%n",
                    orderId, username, fullName, address, phone, foodDetails, orderDate, updateTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
