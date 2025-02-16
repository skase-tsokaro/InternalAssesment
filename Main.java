import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
 class MainPage {
    private JFrame frame;

    public MainPage() {
        // Create the main frame
        frame = new JFrame("Insurance Company");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());


        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to XYZ Insurance", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(welcomeLabel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Sign Up Button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignUpPanel();
            }
        });

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginPanel();
            }
        });

        buttonPanel.add(signUpButton);
        buttonPanel.add(loginButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Set frame visibility
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

     private void openSignUpPanel() {
         JPanel signUpPanel = new JPanel(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.insets = new Insets(5, 5, 5, 5);
         gbc.fill = GridBagConstraints.HORIZONTAL;

         // Title
         JLabel signUpLabel = new JLabel("Sign Up Page", SwingConstants.CENTER);
         signUpLabel.setFont(new Font("Arial", Font.BOLD, 16));
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.gridwidth = 2;
         signUpPanel.add(signUpLabel, gbc);

         // Name Field
         gbc.gridwidth = 1;
         gbc.gridy++;
         gbc.gridx = 0;
         signUpPanel.add(new JLabel("Name:"), gbc);

         JTextField nameField = new JTextField(15);
         gbc.gridx = 1;
         signUpPanel.add(nameField, gbc);

         // Password Field
         gbc.gridy++;
         gbc.gridx = 0;
         signUpPanel.add(new JLabel("Password:"), gbc);

         JPasswordField passwordField = new JPasswordField(15);
         gbc.gridx = 1;
         signUpPanel.add(passwordField, gbc);

         // Admin Checkbox
         gbc.gridy++;
         gbc.gridx = 0;
         signUpPanel.add(new JLabel("Admin:"), gbc);

         JCheckBox adminCheckbox = new JCheckBox();
         gbc.gridx = 1;
         signUpPanel.add(adminCheckbox, gbc);

         // Submit Button
         gbc.gridy++;
         gbc.gridx = 0;
         gbc.gridwidth = 2;
         JButton submitButton = new JButton("Submit");
         submitButton.addActionListener(e -> {
             String name = nameField.getText();
             String password = new String(passwordField.getPassword());
             boolean isAdmin = adminCheckbox.isSelected();

             // Validate input
             if (name.isEmpty() || password.isEmpty()) {
                 JOptionPane.showMessageDialog(frame,
                         "Name and password cannot be empty.",
                         "Error",
                         JOptionPane.ERROR_MESSAGE);
                 return;
             }

             // Save to database
             try (Connection connection = DriverManager.getConnection(
                     "jdbc:mysql://localhost:3306/InsuranceDB", "root", "password123")) {
                 String insertQuery = "INSERT INTO users (name, password, is_admin) VALUES (?, ?, ?)";
                 PreparedStatement statement = connection.prepareStatement(insertQuery);
                 statement.setString(1, name);
                 statement.setString(2, password); // Consider hashing the password for security
                 statement.setBoolean(3, isAdmin);

                 int rowsInserted = statement.executeUpdate();
                 if (rowsInserted > 0) {
                     JOptionPane.showMessageDialog(frame,
                             "Sign up successful!",
                             "Success",
                             JOptionPane.INFORMATION_MESSAGE);
                     switchToMainPage();
                 } else {
                     JOptionPane.showMessageDialog(frame,
                             "Sign up failed. Please try again.",
                             "Error",
                             JOptionPane.ERROR_MESSAGE);
                 }
             } catch (Exception ex) {
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(frame,
                         "Error connecting to the database: " + ex.getMessage(),
                         "Database Error",
                         JOptionPane.ERROR_MESSAGE);
             }
         });
         signUpPanel.add(submitButton, gbc);

         // Back Button
         gbc.gridy++;
         JButton backButton = new JButton("Back to Main Page");
         backButton.addActionListener(e -> switchToMainPage());
         signUpPanel.add(backButton, gbc);

         // Replace content
         frame.setContentPane(signUpPanel);
         frame.revalidate();
         frame.repaint();
     }


     private void openLoginPanel() {
         JPanel loginPanel = new JPanel(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.insets = new Insets(5, 5, 5, 5);
         gbc.fill = GridBagConstraints.HORIZONTAL;

         // Title
         JLabel loginLabel = new JLabel("Login Page", SwingConstants.CENTER);
         loginLabel.setFont(new Font("Arial", Font.BOLD, 16));
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.gridwidth = 2;
         loginPanel.add(loginLabel, gbc);

         // Name Field
         gbc.gridwidth = 1;
         gbc.gridy++;
         gbc.gridx = 0;
         loginPanel.add(new JLabel("Name:"), gbc);

         JTextField nameField = new JTextField(15);
         gbc.gridx = 1;
         loginPanel.add(nameField, gbc);

         // Password Field
         gbc.gridy++;
         gbc.gridx = 0;
         loginPanel.add(new JLabel("Password:"), gbc);

         JPasswordField passwordField = new JPasswordField(15);
         gbc.gridx = 1;
         loginPanel.add(passwordField, gbc);

         // Submit Button
         gbc.gridy++;
         gbc.gridx = 0;
         gbc.gridwidth = 2;
         JButton submitButton = new JButton("Submit");
         submitButton.addActionListener(e -> {
             String name = nameField.getText();
             String password = new String(passwordField.getPassword());

             if (name.isEmpty() || password.isEmpty()) {
                 JOptionPane.showMessageDialog(frame,
                         "Name and Password fields cannot be empty.",
                         "Input Error",
                         JOptionPane.ERROR_MESSAGE);
                 return;
             }

             try (Connection connection = DriverManager.getConnection(
                     "jdbc:mysql://localhost:3306/InsuranceDB", "root", "password123")) {

                 // Prepare the SQL query to check credentials
                 String loginQuery = "SELECT * FROM users WHERE name = ? AND password = ?";
                 PreparedStatement statement = connection.prepareStatement(loginQuery);
                 statement.setString(1, name);
                 statement.setString(2, password);

                 // Execute the query
                 ResultSet resultSet = statement.executeQuery();

                 // Check if a matching user exists
                 if (resultSet.next()) {
                     JOptionPane.showMessageDialog(frame,
                             "Login successful! Welcome " + name,
                             "Success",
                             JOptionPane.INFORMATION_MESSAGE);

                     // Proceed to main menu
                     openMainMenu();
                 } else {
                     JOptionPane.showMessageDialog(frame,
                             "Invalid name or password. Please try again.",
                             "Login Failed",
                             JOptionPane.ERROR_MESSAGE);
                 }

             } catch (Exception ex) {
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(frame,
                         "Error connecting to the database: " + ex.getMessage(),
                         "Database Error",
                         JOptionPane.ERROR_MESSAGE);
             }
         });
         loginPanel.add(submitButton, gbc);

         // Back Button
         gbc.gridy++;
         JButton backButton = new JButton("Back to Main Page");
         backButton.addActionListener(e -> switchToMainPage());
         loginPanel.add(backButton, gbc);

         // Replace content
         frame.setContentPane(loginPanel);
         frame.revalidate();
         frame.repaint();
     }

     private void switchToMainPage() {
         frame.getContentPane().removeAll();
         new MainPage();
     }

     private void openMainMenu() {
         JPanel mainMenuPanel = new JPanel(new GridLayout(0, 1));
         JLabel menuLabel = new JLabel("Main Menu", SwingConstants.CENTER);
         menuLabel.setFont(new Font("Arial", Font.BOLD, 18));
         mainMenuPanel.add(menuLabel);

         JButton option1 = new JButton("Option 1");
         JButton option2 = new JButton("Option 2");
         JButton logoutButton = new JButton("Logout");

         // Add action listeners
         logoutButton.addActionListener(e -> switchToMainPage());

         mainMenuPanel.add(option1);
         mainMenuPanel.add(option2);
         mainMenuPanel.add(logoutButton);

         frame.setContentPane(mainMenuPanel);
         frame.revalidate();
         frame.repaint();
     }

     public static void main(String[] args) {
         SwingUtilities.invokeLater(MainPage::new);
     }
 }

