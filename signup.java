import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;

public class signup extends JDialog {
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JPasswordField confirmPasswordField = new JPasswordField(15);
    private final JFrame parentFrame;

    public signup(JFrame parent) {
        super(parent, "Create New Account", true);
        this.parentFrame = parent;
        initializeUI();
    }

    private void initializeUI() {
        setSize(350, 300);
        setLocationRelativeTo(parentFrame);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username components
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password components
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        // Confirm Password components
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton signUpButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back");

        signUpButton.addActionListener(e -> attemptRegistration());
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(signUpButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void attemptRegistration() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        // Validation checks
        if (username.isEmpty() || password.length == 0 || confirmPassword.length == 0) {
            showError("All fields must be filled!");
            return;
        }

        if (!Arrays.equals(password, confirmPassword)) {
            showError("Passwords do not match!");
            return;
        }

        if (usernameExists(username)) {
            showError("Username already taken!");
            return;
        }

        if (registerUser(username, new String(password))) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Registration failed. Please try again.");
        }

        // Clear password fields for security
        Arrays.fill(password, ' ');
        Arrays.fill(confirmPassword, ' ');
    }

    private boolean usernameExists(String username) {
        String query = "SELECT name FROM users WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
            return true; // Prevent registration on error
        }
    }

    private boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (name, password, is_admin) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // In real application, hash the password
            stmt.setBoolean(3, false);   // Default non-admin user

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // Database connection utility class
    private static class DatabaseUtil {
        private static final String URL = "jdbc:mysql://localhost:3306/InsuranceDB";
        private static final String USER = "root";
        private static final String PASSWORD = "password123";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
}