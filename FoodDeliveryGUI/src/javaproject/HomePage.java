package javaproject;

import javax.swing.*;
import java.awt.*;

public class HomePage {
    private JFrame frame;

    public HomePage(String username, String role) {
        frame = new JFrame("Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Welcome message with username and role
        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (" + role + ")!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Logout and conditional Select Items buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // Show the Select Items button only if the role is "Customer"
        if ("Customer".equalsIgnoreCase(role)) {
            JButton selectItemsButton = new JButton("Select Items");
            selectItemsButton.addActionListener(e -> {
                frame.dispose(); // Close the HomePage
                SwingUtilities.invokeLater(() -> new CombinedPage(username, role).createAndShowGUI()); // Open SelectItemsPage
            });
            buttonPanel.add(selectItemsButton); // Add the Select Items button to the panel
        }

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            frame.dispose(); // Close the home page
            SwingUtilities.invokeLater(() -> new LoginSignup().createAndShowGUI()); // Redirect to login/signup
        });

        // Add the Logout button to the panel
        buttonPanel.add(logoutButton);

        // Add components to the main panel
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(welcomeLabel, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Now we define the createAndShowGUI method so it can be called
    public void createAndShowGUI(String username, String role) {
        new HomePage(username, role); // Create and show the HomePage when invoked
    }
}
