/* Some of the libraries used for this Application */
import com.toedter.calendar.JDateChooser;
import javax.swing.AbstractCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
public class MainWindow {
    private Sorting sorting;
    private JFrame frame;
    private JTable clientsTable;
    private SearchFeature searchFeature;

    public MainWindow() {
        initialize();
    }
    private int idCounter = 1;
    private int id;
    public void initialize() {
        // Initialize main frame
        frame = new JFrame("Insurance Management System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Initialize table and load data FIRST
        clientsTable = new JTable();
        fetchInitialData(); // Load data into table

        // Initialize features
        searchFeature = new SearchFeature(
                clientsTable,
                "jdbc:mysql://localhost:3306/InsuranceDB",
                "root",
                "password123"
        );
        sorting = new Sorting(clientsTable);

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(clientsTable);

        // Create top panel (search + sort)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(searchFeature.getSearchPanel(), BorderLayout.CENTER);
        topPanel.add(sorting.getSortButton(), BorderLayout.EAST);

        // Create control panel (Add Client/Refresh/Profit buttons)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addClientButton = new JButton("Add Client");
        addClientButton.addActionListener(e -> openAddClientWindow());
        controlPanel.add(addClientButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        controlPanel.add(refreshButton);

        JButton profitButton = new JButton("Show Profit");
        profitButton.addActionListener(e -> openProfitWindow());
        controlPanel.add(profitButton);

        // Create logout panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logOutButton = new JButton("Log Out");
        logOutButton.addActionListener(e -> logOut());
        logoutPanel.add(logOutButton);

        // Create combined header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(controlPanel, BorderLayout.CENTER);
        headerPanel.add(logoutPanel, BorderLayout.SOUTH);

        // Add components to frame
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Finalize frame
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);
    }
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton addClientButton = new JButton("Add Client");
        addClientButton.addActionListener(e -> openAddClientWindow());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());

        JButton profitButton = new JButton("Show Profit");
        profitButton.addActionListener(e -> openProfitWindow());

        panel.add(addClientButton);
        panel.add(refreshButton);
        panel.add(profitButton);

        return panel;
    }
    private void fetchInitialData() {
        // Open a connection to the 'InsuranceDB' database using try-with-resources to ensure closure
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/InsuranceDB", "root", "password123");
             // Create a Statement object for executing SQL queries
             Statement statement = connection.createStatement()) {

            // Execute SQL query to fetch all records from the 'clients' table
            ResultSet rs = statement.executeQuery("SELECT * FROM clients");

            // Create a DefaultTableModel to hold table data for the JTable
            DefaultTableModel model = new DefaultTableModel();

            // Retrieve metadata about the ResultSet to dynamically handle column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Loop through metadata to add each column name to the model
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Iterate over the ResultSet to populate the model with row data
            while (rs.next()) {
                // Create an array to hold column data for the current row
                Object[] row = new Object[columnCount];
                // Retrieve each column's value and store it in the row array
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                // Add the populated row to the table model
                model.addRow(row);
            }

            // Set the model for the JTable 'clientsTable' to display the fetched data
            clientsTable.setModel(model);

            // Call a helper method to apply custom date editors on the table if needed
            applyDateEditors();

        } catch (SQLException ex) {
            // Handle any SQL exceptions by printing the stack trace for debugging
            ex.printStackTrace();
        }
    }

    private void applyDateEditors() {
        for (int i = 0; i < clientsTable.getColumnCount(); i++) {
            String colName = clientsTable.getColumnName(i).toLowerCase();
            if (colName.contains("date")) {
                clientsTable.getColumnModel().getColumn(i).setCellEditor(new DateCellEditor());
                clientsTable.getColumnModel().getColumn(i).setCellRenderer(new DateCellRenderer());
            }
        }
    }

    private void refreshData() {
        fetchInitialData();
    }

    public void close() {
        if (searchFeature != null) {
            searchFeature.shutdown();
        }
        frame.dispose();
    }
    private void logOut()
    {
        frame.dispose();
        SwingUtilities.invokeLater(() -> new login());
    }

    private void openAddClientWindow() {
        // Create a new frame for adding a client
        JFrame addClientFrame = new JFrame("Add Client");
        addClientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addClientFrame.setSize(400, 600);
        addClientFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add form fields
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(15);
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(15);
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(15);
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(15);
        JLabel postalCodeLabel = new JLabel("Postal Code:");
        JTextField postalCodeField = new JTextField(15);
        JLabel dateOfBirthLabel = new JLabel("Date of Birth:");
        JDateChooser dateOfBirthChooser = new JDateChooser(); // JCalendar component
        JLabel appointmentDateLabel = new JLabel("Appointment Date:");
        JDateChooser appointmentDateChooser = new JDateChooser(); // JCalendar component
        JLabel dateCalledLabel = new JLabel("Date Called:");
        JDateChooser dateCalledChooser = new JDateChooser(); // JCalendar component

        // Insurance Type dropdown
        JLabel insuranceTypeLabel = new JLabel("Insurance Type:");
        String[] insuranceTypes = {"health insurance", "home insurance", "car insurance", "life insurance"};
        JComboBox<String> insuranceTypeDropdown = new JComboBox<>(insuranceTypes);

        // Insurance Plan dropdown
        JLabel insurancePlanLabel = new JLabel("Insurance Plan:");
        String[] insurancePlans = {"Basic Plan", "Gold Plan"};
        JComboBox<String> insurancePlanDropdown = new JComboBox<>(insurancePlans);

        // Add components to the frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        addClientFrame.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(lastNameLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(addressLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(phoneLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(postalCodeLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(postalCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(dateOfBirthLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(dateOfBirthChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(appointmentDateLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(appointmentDateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(dateCalledLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(dateCalledChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(insuranceTypeLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(insuranceTypeDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        addClientFrame.add(insurancePlanLabel, gbc);

        gbc.gridx = 1;
        addClientFrame.add(insurancePlanDropdown, gbc);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // Get data from text fields
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();
            String postalCode = postalCodeField.getText();
            String insuranceType = (String) insuranceTypeDropdown.getSelectedItem();
            String insurancePlan = (String) insurancePlanDropdown.getSelectedItem();

            // Get the date values from the JDateChooser
            java.util.Date dateOfBirth = dateOfBirthChooser.getDate();
            java.util.Date appointmentDate = appointmentDateChooser.getDate();
            java.util.Date dateCalled = dateCalledChooser.getDate();

            // Save data to the database
            saveClientData(id, firstName, lastName, address, phone, postalCode, dateOfBirth, appointmentDate, dateCalled, insuranceType, insurancePlan);
        });
        buttonsPanel.add(submitButton);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            addClientFrame.dispose(); // Close the "Add Client" window
        });
        buttonsPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        addClientFrame.add(buttonsPanel, gbc);

        // Display the Add Client window
        addClientFrame.setVisible(true);
    }
    //method to open the main menu
    private void openAppointmentsWindow() {

        // Create a new frame for showing appointments
        JFrame appointmentsFrame = new JFrame("Clients");
        appointmentsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        appointmentsFrame.setSize(600, 400);

        // Create a JTable to display client data
        JTable clientsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        appointmentsFrame.add(scrollPane, BorderLayout.CENTER);

        // Create buttons for saving or canceling changes
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add button panel to frame
        appointmentsFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Fetch client data from the database and fill the table
        fetchClientsData(clientsTable);

        // Save button action listener
        saveButton.addActionListener(e -> {
            saveClientsChanges(clientsTable);
            fetchClientsData(clientsTable); // Refresh data from the database
            JOptionPane.showMessageDialog(appointmentsFrame, "Changes saved successfully!");
        });
        // Cancel button action listener
        cancelButton.addActionListener(e -> {
            appointmentsFrame.dispose(); // Close the appointments window
        });

        // Display the appointments frame
        appointmentsFrame.setVisible(true);
    }

    private int getNextClientId() {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/InsuranceDB", "root", "password123")) {

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM clients");

            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1;  // If table is empty
        } catch (SQLException e) {
            System.err.println("Error getting next ID: " + e.getMessage());
            return -1;  // Error state
        }
    }

    private void saveClientData(int id, String firstName, String lastName, String address, String phone, String postalCode,
                                java.util.Date dateOfBirth, java.util.Date appointmentDate, java.util.Date dateCalled,
                                String insuranceType, String insurancePlan) {

        // try-with-resources to automatically close the database connection
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/InsuranceDB", "root", "password123")) {
            // Debugging: confirm successful database connection
            System.out.println("Connected to database successfully");

            // SQL query with parameter placeholders for safe insertion
            String query = "INSERT INTO clients (id, first_name, last_name, address, phone_number, postal_code, " +
                    "date_of_birth, appointment_date, date_called, insurance_type, insurance_plan) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // Create prepared statement to prevent SQL injection
            PreparedStatement statement = connection.prepareStatement(query);

            // Debugging: show key client details being inserted
            System.out.println("[DEBUG] Inserting new client with ID: " + id);
            System.out.println("First Name: " + firstName);
            System.out.println("Insurance Plan: " + insurancePlan);

            // Set parameters in correct order in the table (1-11)
            statement.setInt(1, id);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, address);
            statement.setString(5, phone);
            statement.setString(6, postalCode);

            // Convert java.util.Date to java.sql.Date and handle potential null values
            if (dateOfBirth != null) {
                statement.setDate(7, new java.sql.Date(dateOfBirth.getTime()));
            } else {
                statement.setNull(7, Types.DATE); // Set NULL if no date provided
            }

            if (appointmentDate != null) {
                statement.setDate(8, new java.sql.Date(appointmentDate.getTime()));
            } else {
                statement.setNull(8, Types.DATE);
            }

            if (dateCalled != null) {
                statement.setDate(9, new java.sql.Date(dateCalled.getTime()));
            } else {
                statement.setNull(9, Types.DATE);
            }

            statement.setString(10, insuranceType);
            statement.setString(11, insurancePlan);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Client saved successfully");
                // Increment ID counter only after successful insertion
                idCounter++;
            } else {
                System.out.println("No rows affected during insertion");
            }

        } catch (SQLException e) {
            // Handle database errors and show user-friendly message
            System.err.println("SQL Exception occurred: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Error saving client: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print full error details for debugging
        }
    }
    private void openProfitWindow() {
        JFrame profitFrame = new JFrame("Client Payments and Total Profit");
        profitFrame.setSize(800, 600);
        profitFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profitFrame.setLayout(new BorderLayout());

        // Column headers for the table
        String[] columnNames = {"First Name", "Last Name", "Insurance Type", "Insurance Plan", "Amount Paid"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable profitTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(profitTable);
        profitFrame.add(scrollPane, BorderLayout.CENTER);

        String url = "jdbc:mysql://localhost:3306/InsuranceDB";
        String user = "root";
        String password = "password123";
        String query = "SELECT first_name, last_name, insurance_type, insurance_plan FROM clients";

        double totalRevenue = 0;

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String insuranceType = resultSet.getString("insurance_type");
                String insurancePlan = resultSet.getString("insurance_plan");

                // Calculate the amount paid by the client
                double amountPaid = calculatePayment(insuranceType, insurancePlan);
                totalRevenue += amountPaid;

                // Add data to the table
                tableModel.addRow(new Object[]{firstName, lastName, insuranceType, insurancePlan, "€" + amountPaid});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving client data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate total profit (50% of total revenue)
        double totalProfit = totalRevenue * 0.5;

        // Add total profit label at the bottom
        JLabel profitLabel = new JLabel("Total Profit (50% of Revenue): €" + totalProfit, SwingConstants.CENTER);
        profitLabel.setFont(new Font("Arial", Font.BOLD, 16));
        profitFrame.add(profitLabel, BorderLayout.SOUTH);

        profitFrame.setVisible(true);
    }

    private double calculatePayment(String insuranceType, String insurancePlan) {
        double basicRate = 0, goldRate = 0;

        switch (insuranceType) {
            case "health insurance":
                basicRate = 200;
                goldRate = 2556;
                break;
            case "home insurance":
                basicRate = 250;
                goldRate = 3000;
                break;
            case "car insurance":
                basicRate = 220;
                goldRate = 2640;
                break;
            case "life insurance":
                basicRate = 2530;
                goldRate = 5400;
                break;
        }
        return insurancePlan.equals("Basic Plan") ? basicRate : goldRate;
    }


    private void fetchClientsData(JTable table) {
        String url = "jdbc:mysql://localhost:3306/InsuranceDB";
        String user = "root";
        String password = "password123";
        String query = "SELECT * FROM clients";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Get metadata for column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create column name array
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            // Create table model with date editing support
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    // Return proper class type for date columns
                    String colName = getColumnName(columnIndex).toLowerCase();
                    if (colName.contains("date")) {
                        return java.util.Date.class;
                    }
                    return Object.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 0; // Keep ID column non-editable
                }
            };

            // Populate table model with data
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            // Set model to table
            table.setModel(tableModel);

            // Set custom editors/renderers for date columns
            for (int i = 0; i < table.getColumnCount(); i++) {
                String colName = table.getColumnName(i).toLowerCase();
                if (colName.equals("date_of_birth") ||
                        colName.equals("appointment_date") ||
                        colName.equals("date_called")) {

                    table.getColumnModel().getColumn(i).setCellRenderer(new DateCellRenderer());
                    table.getColumnModel().getColumn(i).setCellEditor(new DateCellEditor());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error fetching client data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class DateCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JDateChooser dateChooser = new JDateChooser();

        public DateCellEditor() {
            dateChooser.setDateFormatString("yyyy-MM-dd");
        }

        @Override
        public Object getCellEditorValue() {
            return dateChooser.getDate();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            dateChooser.setDate((value instanceof Date) ? (Date) value : null);
            return dateChooser;
        }
    }

    private class DateCellRenderer extends DefaultTableCellRenderer {
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Date) {
                setText(sdf.format((Date) value));
            } else {
                setText("");
            }
            return this;
        }
    }

    private void saveClientsChanges(JTable table) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        // Get column indices by name
        final int idCol = getColumnIndex(table, "id");
        final int firstNameCol = getColumnIndex(table, "first_name");
        final int lastNameCol = getColumnIndex(table, "last_name");
        final int addressCol = getColumnIndex(table, "address");
        final int phoneCol = getColumnIndex(table, "phone_number");
        final int postalCodeCol = getColumnIndex(table, "postal_code");
        final int dobCol = getColumnIndex(table, "date_of_birth");
        final int apptDateCol = getColumnIndex(table, "appointment_date");
        final int dateCalledCol = getColumnIndex(table, "date_called");
        final int insuranceTypeCol = getColumnIndex(table, "insurance_type");
        final int insurancePlanCol = getColumnIndex(table, "insurance_plan");

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            try {
                int id = ((Number) tableModel.getValueAt(row, idCol)).intValue();
                String firstName = safeGetString(tableModel, row, firstNameCol);
                String lastName = safeGetString(tableModel, row, lastNameCol);
                String address = safeGetString(tableModel, row, addressCol);
                String phoneNumber = safeGetString(tableModel, row, phoneCol);
                String postalCode = safeGetString(tableModel, row, postalCodeCol);
                java.sql.Date dateOfBirth = convertToSqlDate(tableModel.getValueAt(row, dobCol));
                java.sql.Date appointmentDate = convertToSqlDate(tableModel.getValueAt(row, apptDateCol));
                java.sql.Date dateCalled = convertToSqlDate(tableModel.getValueAt(row, dateCalledCol));
                String insuranceType = safeGetString(tableModel, row, insuranceTypeCol);
                String insurancePlan = safeGetString(tableModel, row, insurancePlanCol);

                boolean success = updateClientInDatabase(
                        id, firstName, lastName, address, phoneNumber, postalCode,
                        dateOfBirth, appointmentDate, dateCalled, insuranceType, insurancePlan
                );

                System.out.println("[DEBUG] Client ID " + id + " update " + (success ? "successful" : "failed"));

            } catch (Exception e) {
                System.err.println("Error processing row " + row + ":");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error updating row " + (row + 1) + ": " + e.getMessage(),
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper methods for safe data retrieval
    private String safeGetString(DefaultTableModel model, int row, int col) {
        Object value = model.getValueAt(row, col);
        return (value != null) ? value.toString() : "";
    }

    private Date safeGetDate(DefaultTableModel model, int row, int col) {
        Object value = model.getValueAt(row, col);
        if (value instanceof Date) {
            return new Date(((Date) value).getTime());
        }
        return null;
    }

    private int getColumnIndex(JTable table, String columnName) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column not found: " + columnName);
    }


    private java.sql.Date convertToSqlDate(Object value) {
        if (value instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) value).getTime());
        }
        return null;
    }
    private boolean updateClientInDatabase(int id, String firstName, String lastName, String address,
                                           String phoneNumber, String postalCode,
                                           java.sql.Date dateOfBirth,
                                           java.sql.Date appointmentDate,
                                           java.sql.Date dateCalled,
                                           String insuranceType, String insurancePlan)  {
        String url = "jdbc:mysql://localhost:3306/InsuranceDB";
        String user = "root";
        String password = "password123";

        String updateQuery = "UPDATE clients SET first_name = ?, last_name = ?, address = ?, phone_number = ?, " +
                "postal_code = ?, date_of_birth = ?, appointment_date = ?, date_called = ?, " +
                "insurance_type = ?, insurance_plan = ? WHERE id = ?"; // 11 parameters

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            // Bind parameters in CORRECT order
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, address);
            statement.setString(4, phoneNumber);
            statement.setString(5, postalCode);

            // Handle dates (including nulls)
            if (dateOfBirth != null) {
                statement.setDate(6, dateOfBirth);
            } else {
                statement.setNull(6, Types.DATE);
            }

            if (appointmentDate != null) {
                statement.setDate(7, appointmentDate);
            } else {
                statement.setNull(7, Types.DATE);
            }

            if (dateCalled != null) {
                statement.setDate(8, dateCalled);
            } else {
                statement.setNull(8, Types.DATE);
            }

            statement.setString(9, insuranceType);
            statement.setString(10, insurancePlan);
            statement.setInt(11, id); // Correct position for WHERE clause

            int rowsUpdated = statement.executeUpdate();
            System.out.println("[DEBUG] Rows affected for ID " + id + ": " + rowsUpdated);
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error updating client ID " + id + ":");
            e.printStackTrace();
            return false;
        }
    }

}
