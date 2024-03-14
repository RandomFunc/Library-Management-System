package assignment1;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class Library {
    static final String DB_URL = "jdbc:mysql://localhost:3306/login"; // Assuming database name is login
    static final String DB_USER = "root";
    static final String DB_PASSWD = "kali@123";
    static final String LOGIN_QUERY = "SELECT adminID, adminpass FROM admin";
    static final String INSERT_BOOK_QUERY = "INSERT INTO books (bookId, bookname, bookauthor, bookgenre, bookavail) VALUES (?, ?, ?, ?, ?)";
    static final String INSERT_USER_QUERY = "INSERT INTO users (userID, username, useremail, usercont, userpass) VALUES (?, ?, ?, ?, ?)";
    static final String DISPLAY_BOOKS_QUERY = "SELECT * FROM books";
    static final String CHECKOUT_QUERY = "UPDATE books SET bookavail = false WHERE bookname = ? AND bookavail = true";
    static final String ADD_TO_BORROWED_QUERY = "INSERT INTO borrowedbooks (bkname, urname) VALUES (?, ?)";
    static final String RETURN_BOOK_QUERY = "UPDATE books SET bookavail = true WHERE bookname = ?";
    static final String REMOVE_FROM_BORROWED_QUERY = "DELETE FROM borrowedbooks WHERE bkname = ?";
    static final String SHOW_BORROWED_BOOKS_QUERY = "SELECT bkname FROM borrowedbooks WHERE urname = ?";
    static final String CHECK_USER_EXIST_QUERY = "SELECT * FROM users WHERE userID = ?";

        public static void checkoutBook() {
        JFrame checkoutFrame = new JFrame("Checkout Book");
        checkoutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel bookLabel = new JLabel("Book Name:");
        JTextField bookTextField = new JTextField(20);
        JLabel userLabel = new JLabel("User Name:");
        JTextField userTextField = new JTextField(20);
        JButton checkoutButton = new JButton("Checkout");

        checkoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookName = bookTextField.getText();
                String userName = userTextField.getText();

                if (checkout(bookName, userName)) {
                    JOptionPane.showMessageDialog(checkoutFrame, "Book checked out successfully!");
                } else {
                    JOptionPane.showMessageDialog(checkoutFrame, "Failed to checkout book!");
                }
            }
        });

        JPanel checkoutPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        checkoutPanel.add(bookLabel, gbc);
        gbc.gridy++;
        checkoutPanel.add(bookTextField, gbc);
        gbc.gridy++;
        checkoutPanel.add(userLabel, gbc);
        gbc.gridy++;
        checkoutPanel.add(userTextField, gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        checkoutPanel.add(checkoutButton, gbc);

        checkoutFrame.getContentPane().add(checkoutPanel);
        checkoutFrame.pack();
        checkoutFrame.setLocationRelativeTo(null);
        checkoutFrame.setVisible(true);
    }

    public static boolean checkout(String bookName, String userName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmtCheckout = conn.prepareStatement(CHECKOUT_QUERY);
             PreparedStatement pstmtAddToBorrowed = conn.prepareStatement(ADD_TO_BORROWED_QUERY)) {

            pstmtCheckout.setString(1, bookName);
            int updatedRows = pstmtCheckout.executeUpdate();

            if (updatedRows > 0) {
                pstmtAddToBorrowed.setString(1, bookName);
                pstmtAddToBorrowed.setString(2, userName);
                pstmtAddToBorrowed.executeUpdate();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Book is not available!");
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean validateLogin(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement stmt = conn.prepareStatement(LOGIN_QUERY)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String dbUsername = rs.getString("adminID");
                String dbPassword = rs.getString("adminpass");
                if (username.equals(dbUsername) && password.equals(dbPassword)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void openAddBookInterface() {
        // Create the JFrame for adding books
        JFrame mainFrame = new JFrame("Add Book");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components for adding books
        JLabel titleLabel = new JLabel("Title:");
        JTextField titleTextField = new JTextField(20);
        JLabel authorLabel = new JLabel("Author:");
        JTextField authorTextField = new JTextField(20);
        JLabel genreLabel = new JLabel("Genre:");
        JTextField genreTextField = new JTextField(20);
        JButton addButton = new JButton("Add Book");

        // Add action listener to the add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = titleTextField.getText();
                String author = authorTextField.getText();
                String genre = genreTextField.getText();

                if (addBook(title, author, genre)) {
                    JOptionPane.showMessageDialog(mainFrame, "Book added successfully!");
                    // Clear text fields after successful addition
                    titleTextField.setText("");
                    authorTextField.setText("");
                    genreTextField.setText("");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to add book!");
                }
            }
        });

        // Create panel for adding books and add components
        JPanel addBookPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        addBookPanel.add(titleLabel, gbc);
        gbc.gridy++;
        addBookPanel.add(titleTextField, gbc);
        gbc.gridy++;
        addBookPanel.add(authorLabel, gbc);
        gbc.gridy++;
        addBookPanel.add(authorTextField, gbc);
        gbc.gridy++;
        addBookPanel.add(genreLabel, gbc);
        gbc.gridy++;
        addBookPanel.add(genreTextField, gbc);
        gbc.gridy++;
        addBookPanel.add(addButton, gbc);

        // Add add book panel to main frame and display
        mainFrame.getContentPane().add(addBookPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null); // Center the frame
        mainFrame.setVisible(true);
    }

    public static void openAddUserInterface() {
        // Create the JFrame for adding users
        JFrame mainFrame = new JFrame("Add User");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components for adding users
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameTextField = new JTextField(20);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailTextField = new JTextField(20);
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneTextField = new JTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);
        JButton addButton = new JButton("Add User");

        // Add action listener to the add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameTextField.getText();
                String email = emailTextField.getText();
                String phone = phoneTextField.getText();
                String password = new String(passField.getPassword());

                if (addUser(name, email, phone, password)) {
                    JOptionPane.showMessageDialog(mainFrame, "User added successfully!");
                    // Clear text fields after successful addition
                    nameTextField.setText("");
                    emailTextField.setText("");
                    phoneTextField.setText("");
                    passField.setText("");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to add user!");
                }
            }
        });

        // Create panel for adding users and add components
        JPanel addUserPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        addUserPanel.add(nameLabel, gbc);
        gbc.gridy++;
        addUserPanel.add(nameTextField, gbc);
        gbc.gridy++;
        addUserPanel.add(emailLabel, gbc);
        gbc.gridy++;
        addUserPanel.add(emailTextField, gbc);
        gbc.gridy++;
        addUserPanel.add(phoneLabel, gbc);
        gbc.gridy++;
        addUserPanel.add(phoneTextField, gbc);
        gbc.gridy++;
        addUserPanel.add(passLabel, gbc);
        gbc.gridy++;
        addUserPanel.add(passField, gbc);
        gbc.gridy++;
        addUserPanel.add(addButton, gbc);

        // Add add user panel to main frame and display
        mainFrame.getContentPane().add(addUserPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null); // Center the frame
        mainFrame.setVisible(true);
    }

    public static boolean addBook(String title, String author, String genre) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmt = conn.prepareStatement(INSERT_BOOK_QUERY)) {
            pstmt.setString(1, generateBookId());
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, genre);
            pstmt.setBoolean(5, true); // Setting bookavail to true
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean addUser(String name, String email, String phone, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_QUERY)) {
            pstmt.setString(1, generateUserId());
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void displayBooksGUI() {
        JFrame booksFrame = new JFrame("Books");
        booksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel booksPanel = new JPanel(new GridLayout(0, 1));

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmt = conn.prepareStatement(DISPLAY_BOOKS_QUERY);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String bookId = rs.getString("bookId");
                String bookName = rs.getString("bookname");
                String author = rs.getString("bookauthor");
                String genre = rs.getString("bookgenre");
                boolean avail = rs.getBoolean("bookavail");

                JLabel bookLabel = new JLabel("Book ID: " + bookId + ", Title: " + bookName + ", Author: " + author + ", Genre: " + genre + ", Availability: " + (avail ? "Available" : "Not Available"));
                booksPanel.add(bookLabel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(booksPanel);
        booksFrame.getContentPane().add(scrollPane);
        booksFrame.pack();
        booksFrame.setLocationRelativeTo(null);
        booksFrame.setVisible(true);
    }

    private static String generateBookId() {
        String bookId = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM books WHERE bookID = ?")) {
            do {
                // Generate a random number between 1000000 and 9999999
                int randomBookId = (int) (Math.random() * 9000000) + 1000000;
                bookId = String.valueOf(randomBookId);
                pstmt.setString(1, bookId);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                int count = rs.getInt("count");
                if (count == 0) {
                    // If the generated book ID is not present in the table, break the loop
                    break;
                }
            } while (true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return bookId;
    }

    private static String generateUserId() {
        String userId = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM users WHERE userID = ?")) {
            do {
                // Generate a random number between 1000000 and 9999999
                int randomUserId = (int) (Math.random() * 9000000) + 1000000;
                userId = String.valueOf(randomUserId);
                pstmt.setString(1, userId);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                int count = rs.getInt("count");
                if (count == 0) {
                    // If the generated user ID is not present in the table, break the loop
                    break;
                }
            } while (true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userId;
    }

    public static void returnBook() {
        JFrame returnFrame = new JFrame("Return Book");
        returnFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel bookLabel = new JLabel("Book Name:");
        JTextField bookTextField = new JTextField(20);
        JLabel userLabel = new JLabel("User Name:");
        JTextField userTextField = new JTextField(20);
        JButton returnButton = new JButton("Return");

        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookName = bookTextField.getText();
                String userName = userTextField.getText();

                if (returnBook(bookName, userName)) {
                    JOptionPane.showMessageDialog(returnFrame, "Book returned successfully!");
                } else {
                    JOptionPane.showMessageDialog(returnFrame, "Failed to return book!");
                }
            }
        });

        JPanel returnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        returnPanel.add(bookLabel, gbc);
        gbc.gridy++;
        returnPanel.add(bookTextField, gbc);
        gbc.gridy++;
        returnPanel.add(userLabel, gbc);
        gbc.gridy++;
        returnPanel.add(userTextField, gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        returnPanel.add(returnButton, gbc);

        returnFrame.getContentPane().add(returnPanel);
        returnFrame.pack();
        returnFrame.setLocationRelativeTo(null);
        returnFrame.setVisible(true);
    }

    public static boolean returnBook(String bookName, String userName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmtReturnBook = conn.prepareStatement(RETURN_BOOK_QUERY);
             PreparedStatement pstmtRemoveFromBorrowed = conn.prepareStatement(REMOVE_FROM_BORROWED_QUERY)) {

            pstmtReturnBook.setString(1, bookName);
            int updatedRows = pstmtReturnBook.executeUpdate();

            if (updatedRows > 0) {
                pstmtRemoveFromBorrowed.setString(1, bookName);
                pstmtRemoveFromBorrowed.executeUpdate();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to return book or book not found!");
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void showBorrowedBooks() {
    JFrame borrowedBooksFrame = new JFrame("Borrowed Books");
    borrowedBooksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    String userId = JOptionPane.showInputDialog("Enter User ID:");
    if (userId != null && !userId.isEmpty()) {
        JPanel borrowedBooksPanel = new JPanel(new GridLayout(0, 1));

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
             PreparedStatement pstmtUserCheck = conn.prepareStatement(CHECK_USER_EXIST_QUERY);
             PreparedStatement pstmtBorrowedBooks = conn.prepareStatement(SHOW_BORROWED_BOOKS_QUERY)) {

            // Check if user exists
            pstmtUserCheck.setString(1, userId);
            ResultSet userResult = pstmtUserCheck.executeQuery();
            if (userResult.next()) {
                // User exists, proceed to fetch borrowed books
                pstmtBorrowedBooks.setString(1, userId);
                ResultSet rs = pstmtBorrowedBooks.executeQuery();
                if (!rs.isBeforeFirst()) {
                    // No books borrowed by this user
                    JOptionPane.showMessageDialog(null, "No books are borrowed by this user.");
                } else {
                    // Display borrowed books
                    while (rs.next()) {
                        String borrowedBookName = rs.getString("bkname");
                        JLabel borrowedBookLabel = new JLabel("Borrowed Book: " + borrowedBookName);
                        borrowedBooksPanel.add(borrowedBookLabel);
                    }
                    JScrollPane scrollPane = new JScrollPane(borrowedBooksPanel);
                    borrowedBooksFrame.getContentPane().add(scrollPane);
                    borrowedBooksFrame.pack();
                    borrowedBooksFrame.setLocationRelativeTo(null);
                    borrowedBooksFrame.setVisible(true);
                }
            } else {
                // User does not exist
                JOptionPane.showMessageDialog(null, "Entered user does not exist.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(null, "Invalid User ID!");
    }
}
}