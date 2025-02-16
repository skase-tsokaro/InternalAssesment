import javax.swing.*;
import java.sql.*;

public class login {

    public login() {
        // Create the login frame
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(null);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 30, 80, 25);
        loginFrame.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(120, 30, 130, 25);
        loginFrame.add(usernameField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 70, 80, 25);
        loginFrame.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(120, 70, 130, 25);
        loginFrame.add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 110, 100, 30);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(loginFrame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.dispose(); // Close the login window
                new MainWindow();    // Open the main menu
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginFrame.add(loginButton);
        // Sign Up button
        // Sign Up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(100, 150, 100, 30);
        signUpButton.addActionListener(e -> {
            loginFrame.dispose(); // Close the login window
            new signup(loginFrame); // Pass the login frame as parent
        });
        loginFrame.add(signUpButton);

        loginFrame.setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/InsuranceDB";
        String dbUser = "root";
        String dbPassword = "password123";

        String query = "SELECT * FROM users WHERE name = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // If a record is found, the credentials are valid
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(login::new);
    }
}
