import com.toedter.calendar.JDateChooser;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SearchFeature {
    private final JTextField searchField;
    private final JTable clientsTable;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private Timer searchTimer;
    private final ScheduledExecutorService executor;

    public SearchFeature(JTable clientsTable, String dbUrl, String dbUser, String dbPassword) {
        this.clientsTable = clientsTable;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.searchField = new JTextField(20);
        setupSearchListener();
    }

    public JComponent getSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(new JLabel("Search:"), BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

    private void setupSearchListener() {
        searchTimer = new Timer(500, e -> performSearch());
        searchTimer.setRepeats(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { triggerSearch(); }
            public void removeUpdate(DocumentEvent e) { triggerSearch(); }
            public void changedUpdate(DocumentEvent e) { triggerSearch(); }

            private void triggerSearch() {
                if (!searchTimer.isRunning()) {
                    searchTimer.restart();
                }
            }
        });
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        executor.schedule(() -> {
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                List<Object[]> rows = new ArrayList<>();
                List<String> columnNames = new ArrayList<>();

                try (PreparedStatement stmt = createSearchStatement(conn, searchTerm);
                     ResultSet rs = stmt.executeQuery()) {

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Get column names and types
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(metaData.getColumnName(i));
                    }

                    // Process results
                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        rows.add(row);
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel model = createTableModel(columnNames, rows);
                    clientsTable.setModel(model);
                    configureDateColumns();
                });

            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null,
                                "Search error: " + ex.getMessage(),
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE));
                ex.printStackTrace();
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    private PreparedStatement createSearchStatement(Connection conn, String searchTerm) throws SQLException {
        String query = buildSearchQuery(searchTerm);
        PreparedStatement stmt = conn.prepareStatement(query);

        if (!searchTerm.isEmpty()) {
            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchPattern);
            }
        }
        return stmt;
    }

    private String buildSearchQuery(String searchTerm) {
        if (searchTerm.isEmpty()) {
            return "SELECT * FROM clients";
        }
        return """
            SELECT * FROM clients 
            WHERE first_name LIKE ? 
            OR last_name LIKE ? 
            OR address LIKE ? 
            OR phone_number LIKE ? 
            OR postal_code LIKE ? 
            OR insurance_type LIKE ?""";
    }

    private DefaultTableModel createTableModel(List<String> columnNames, List<Object[]> rows) {
        return new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                String colName = getColumnName(columnIndex).toLowerCase();
                if (colName.contains("date")) {
                    return Date.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID column not editable
            }

            @Override
            public String getColumnName(int column) {
                return columnNames.get(column);
            }

            @Override
            public int getRowCount() {
                return rows.size();
            }

            @Override
            public int getColumnCount() {
                return columnNames.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                return rows.get(row)[column];
            }
        };
    }

    private void configureDateColumns() {
        TableColumnModel columnModel = clientsTable.getColumnModel();
        TableModel model = clientsTable.getModel();

        for (int i = 0; i < model.getColumnCount(); i++) {
            if (Date.class.isAssignableFrom(model.getColumnClass(i))) {
                TableColumn column = columnModel.getColumn(i);
                column.setCellEditor(new DatePickerEditor());
                column.setCellRenderer(new DateRenderer());
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    // Date Editor/Renderer implementations
    private static class DatePickerEditor extends DefaultCellEditor {
        private final JDateChooser dateChooser;

        public DatePickerEditor() {
            super(new JTextField());
            dateChooser = new JDateChooser();
            dateChooser.setDateFormatString("yyyy-MM-dd");
            editorComponent = dateChooser;
        }

        @Override
        public Object getCellEditorValue() {
            return dateChooser.getDate();
        }
    }

    private static class DateRenderer extends DefaultTableCellRenderer {
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Date) {
                setText(sdf.format((Date) value));
            }
            return c;
        }
    }
}