package schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the schema layout (columns, types, constraints) of a table.
 */
public class Schema {
    private List<Column> columns;

    public Schema() {
        this.columns = new ArrayList<>();
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getColumn(String name) {
        for (Column column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null; // Return null or throw exception based on further iteration
    }

    public boolean containsColumn(String name) {
        return getColumn(name) != null;
    }

    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String toString() {
        return "Schema{" +
                "columns=" + columns +
                '}';
    }
}
