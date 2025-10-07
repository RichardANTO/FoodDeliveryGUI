package javaproject;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

public class CombinedPage {
    private JFrame frame;
    private String username;
    private String role;

    public CombinedPage(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Select Items and Enter Delivery Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel with "Go Back" and "Edit" buttons
        JPanel topPanel = new JPanel(new BorderLayout());

        // Go Back Button
        JButton goBackButton = new JButton("Go Back");
        goBackButton.setFont(new Font("Arial", Font.PLAIN, 14));
        goBackButton.setFocusPainted(false);
        goBackButton.setBackground(new Color(255, 182, 193)); // Light pink
        goBackButton.addActionListener(e -> {
            frame.dispose(); // Close the current page
            SwingUtilities.invokeLater(() -> new HomePage(username, role).createAndShowGUI(username, role)); // Go back to the HomePage
        });

        // Edit Button (Updated to open EditPage)
        JButton editButton = new JButton("Edit");
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));
        editButton.setFocusPainted(false);
        editButton.setBackground(new Color(173, 216, 230)); // Light blue

        editButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                EditPage editPage = new EditPage(username, role);
                editPage.createAndShowGUI();
            });
        });

        topPanel.add(editButton, BorderLayout.WEST);
        topPanel.add(goBackButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Food details panel
        JPanel foodPanel = createFoodPanel();

        // Delivery details panel
        JPanel deliveryPanel = createDeliveryPanel();

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(144, 238, 144)); // Light green
        submitButton.setFocusPainted(false);

        submitButton.addActionListener(e -> {
            try {
                // Validate and save the order
                boolean success = saveToDatabase(foodPanel, deliveryPanel);

                if (success) {
                    JOptionPane.showMessageDialog(frame, "Details submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Optionally, add a delay before going back to HomePage or allow users to stay on the page
                    SwingUtilities.invokeLater(() -> new HomePage(username, role).createAndShowGUI(username, role));
                    frame.dispose(); // Close current page only after everything is done
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(foodPanel, BorderLayout.NORTH);
        centerPanel.add(deliveryPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(submitButton, BorderLayout.SOUTH);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void applyNumericFilter(JTextField textField) {
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9\\.-]")) { // Allow digits, period, and negative sign
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9\\.-]")) { // Allow digits, period, and negative sign
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void applyPhoneNumberFilter(JTextField textField) {
        AbstractDocument document = (AbstractDocument) textField.getDocument();
        document.setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9\\(\\)\\-\\s]")) { // Allow digits, parentheses, hyphens, and spaces
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("[0-9\\(\\)\\-\\s]+")) { // Allow digits, parentheses, hyphens, and spaces
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private JPanel createFoodPanel() {
        JPanel foodPanel = new JPanel(new GridBagLayout());
        foodPanel.setBorder(BorderFactory.createTitledBorder("Select Food Items"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] foodItems = {"Pizza - $10", "Burger - $5", "Pasta - $7", "Carrot", "Onion", "Potato", "Tomato", "Garlic", "Milk"};
        for (int i = 0; i < foodItems.length; i++) {
            JCheckBox foodItem = new JCheckBox(foodItems[i]);
            gbc.gridx = 0;
            gbc.gridy = i;
            foodPanel.add(foodItem, gbc);

            JTextField qtyField = new JTextField(5);
            applyNumericFilter(qtyField); // Apply filter here
            gbc.gridx = 1;
            foodPanel.add(new JLabel("Quantity:"), gbc);
            gbc.gridx = 2;
            foodPanel.add(qtyField, gbc);

            // Create JComboBox for weight with predefined options
            String[] weightOptions = {"100g", "250g", "500g", "1kg", "2kg", "5kg"};
            JComboBox<String> weightComboBox = new JComboBox<>(weightOptions);
            gbc.gridx = 3;
            foodPanel.add(new JLabel(i == 8 ? "Liters:" : "Weight:"), gbc);
            gbc.gridx = 4;
            foodPanel.add(weightComboBox, gbc);
        }

        return foodPanel;
    }

    private JPanel createDeliveryPanel() {
        JPanel deliveryPanel = new JPanel(new GridBagLayout());
        deliveryPanel.setBorder(BorderFactory.createTitledBorder("Delivery Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        deliveryPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        deliveryPanel.add(nameField, gbc);

        JLabel addressLabel = new JLabel("Address:");
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);

        gbc.gridx = 0;
        gbc.gridy = 1;
        deliveryPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        deliveryPanel.add(addressScrollPane, gbc);

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(15);
        applyPhoneNumberFilter(phoneField); // Apply phone number filter here

        gbc.gridx = 0;
        gbc.gridy = 2;
        deliveryPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        deliveryPanel.add(phoneField, gbc);

        return deliveryPanel;
    }

    private boolean saveToDatabase(JPanel foodPanel, JPanel deliveryPanel) throws Exception {
        String url = "jdbc:mysql://localhost:3306/userdb";
        String user = "root";
        String password = "";

        Connection conn = DriverManager.getConnection(url, user, password);

        // Validate delivery details
        JTextField nameField = (JTextField) deliveryPanel.getComponent(1);
        JTextArea addressArea = (JTextArea) ((JScrollPane) deliveryPanel.getComponent(3)).getViewport().getView();
        JTextField phoneField = (JTextField) deliveryPanel.getComponent(5);

        if (nameField.getText().trim().isEmpty() || addressArea.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all delivery details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Create JSON object to store multiple food items
        JSONArray foodArray = new JSONArray();

        for (int i = 0; i < foodPanel.getComponentCount(); i++) {
            Component comp = foodPanel.getComponent(i);
            if (comp instanceof JCheckBox) {
                JCheckBox foodItem = (JCheckBox) comp;
                if (foodItem.isSelected()) {
                    JTextField qtyField = (JTextField) foodPanel.getComponent(i + 2);
                    JComboBox<String> weightComboBox = (JComboBox<String>) foodPanel.getComponent(i + 4);
                    String quantityStr = qtyField.getText().trim();
                    String selectedWeight = (String) weightComboBox.getSelectedItem();

                    JSONObject foodObject = new JSONObject();
                    foodObject.put("food_name", foodItem.getText());
                    foodObject.put("quantity", Integer.parseInt(quantityStr));
                    foodObject.put("weight", selectedWeight);
                    foodArray.put(foodObject);
                }
            }
        }

        // Insert data into database
        LocalDateTime now = LocalDateTime.now();
        Timestamp currentTimestamp = Timestamp.valueOf(now);

        String query = "INSERT INTO final_order (username, full_name, address, phone, food_details, order_date) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, nameField.getText());
        stmt.setString(3, addressArea.getText());
        stmt.setString(4, phoneField.getText());
        stmt.setString(5, foodArray.toString()); // Store food items as JSON string
        stmt.setTimestamp(6, currentTimestamp);

        int result = stmt.executeUpdate();

        conn.close();

        return result > 0;
    }
}
