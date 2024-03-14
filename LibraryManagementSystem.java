package assignment1;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    public static void createAndShowGUI() {
        // Create the JFrame for login
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components for login
        JLabel userLabel = new JLabel("Username:");
        JTextField userTextField = new JTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String password = new String(passField.getPassword());

                if (Library.validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(loginFrame, "Login successful!");
                    // Ask what action the admin wants to take
                    Object[] options = {"Add Book", "Add User", "Display Books", "Checkout Book", "Return Book", "Show Borrowed Books"};
                    int choice = JOptionPane.showOptionDialog(loginFrame, "What would you like to do?", "Action Selection",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            options, options[0]);
                    if (choice == JOptionPane.YES_OPTION) {
                        Library.openAddBookInterface();
                    } else if (choice == JOptionPane.NO_OPTION) {
                        Library.openAddUserInterface();
                    } else if (choice == JOptionPane.CANCEL_OPTION) {
                        Library.displayBooksGUI(); // Modified to display books using GUI
                    } else if (choice == 3) { // Checkout option
                        Library.checkoutBook(); // Invoke the checkout method
                    } else if (choice == 4) { // Return book option
                        Library.returnBook(); // Invoke the return book method
                    } else if (choice == 5) { // Show borrowed books option
                        Library.showBorrowedBooks(); // Invoke the show borrowed books method
                    }
                    loginFrame.dispose(); // Close the login window
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid username or password");
                }
            }
        });

        // Create panel for login and add components
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        loginPanel.add(userLabel, gbc);
        gbc.gridy++;
        loginPanel.add(userTextField, gbc);
        gbc.gridy++;
        loginPanel.add(passLabel, gbc);
        gbc.gridy++;
        loginPanel.add(passField, gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        // Add login panel to login frame and display
        loginFrame.getContentPane().add(loginPanel);
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null); // Center the frame
        loginFrame.setVisible(true);
    }
    }
