import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Sorting {
    private final JButton sortButton; // Correct variable declaration
    private final JTable table;
    private SortingOptions currentSort = SortingOptions.ID;
    private SortOrder sortOrder = SortOrder.ASCENDING;

    public Sorting(JTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        this.sortButton = new JButton("Sort By");
        this.table = table;
        createPopupMenu();
    }

    private void createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        for (SortingOptions option : SortingOptions.values()) {
            JMenuItem menuItem = new JMenuItem(option.getLabel());
            menuItem.addActionListener(e -> handleSortSelection(option));
            popupMenu.add(menuItem);
        }
        sortButton.addActionListener(e ->
                popupMenu.show(sortButton, 0, sortButton.getHeight()));
    }

    private void handleSortSelection(SortingOptions option) {
        // Toggle sort order if the same option is selected
        if (option == currentSort) {
            sortOrder = (sortOrder == SortOrder.ASCENDING) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        } else {
            currentSort = option;
            sortOrder = SortOrder.ASCENDING;
        }
        // Manually sort the table data based on the selected column and order.
        sortTable(option.getColumnIndex(), sortOrder);
    }

    private void sortTable(int columnIndex, SortOrder order) {
        // Ensure the model is of type DefaultTableModel
        if (!(table.getModel() instanceof DefaultTableModel)) {
            JOptionPane.showMessageDialog(null, "Table model must be DefaultTableModel", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();

        // Extract all rows into a list.
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            Object[] rowData = new Object[colCount];
            for (int j = 0; j < colCount; j++) {
                rowData[j] = model.getValueAt(i, j);
            }
            rows.add(rowData);
        }

        // Create a comparator for the chosen column.
        Comparator<Object[]> comparator = (r1, r2) -> {
            Object o1 = r1[columnIndex];
            Object o2 = r2[columnIndex];
            if(o1 == null && o2 == null) return 0;
            if(o1 == null) return -1;
            if(o2 == null) return 1;
            // If both objects are Comparable, use their compareTo method.
            if(o1 instanceof Comparable && o2 instanceof Comparable) {
                try {
                    return ((Comparable) o1).compareTo(o2);
                } catch (ClassCastException ex) {
                    // Fallback to string comparison if types don't match.
                    return o1.toString().compareTo(o2.toString());
                }
            }
            return o1.toString().compareTo(o2.toString());
        };

        // Reverse the comparator if descending order is requested.
        if (order == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        // Sort the rows.
        rows.sort(comparator);

        // Clear the model and add the sorted rows back.
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    public JButton getSortButton() {
        return sortButton;
    }

}