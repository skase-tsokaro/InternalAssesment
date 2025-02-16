public enum SortingOptions {
    ID("ID", 0),
    FIRST_NAME("First Name", 1),
    LAST_NAME("Last Name", 2),
    ADDRESS("Address", 3),
    PHONE("Phone", 4),
    POSTAL_CODE("Postal Code", 5);

    private final String label;
    private final int columnIndex;

    SortingOptions(String label, int columnIndex) {
        this.label = label;
        this.columnIndex = columnIndex;
    }

    public String getLabel() {
        return label;
    }

    public int getColumnIndex() {
        return columnIndex;
    }
}