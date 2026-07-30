package schema;

import exception.SchemaException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the schema layout (columns, types, constraints) of a table.
 */
public class Schema {
    private List<Column> columns;
    private List<ForeignKeyConstraint> foreignKeys;

    public Schema() {
        this.columns = new ArrayList<>();
        this.foreignKeys = new ArrayList<>();
    }

    public void addColumn(Column column) {
        if (column.isPrimaryKey() && getPrimaryKeyColumn() != null) {
            throw new SchemaException("Multiple PRIMARY KEYs defined in table definition.");
        }
        this.columns.add(column);
    }

    public void addForeignKey(ForeignKeyConstraint fk) {
        this.foreignKeys.add(fk);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<ForeignKeyConstraint> getForeignKeys() {
        return foreignKeys;
    }

    public Column getColumn(String name) {
        for (Column column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }

    public boolean containsColumn(String name) {
        return getColumn(name) != null;
    }

    public int getColumnCount() {
        return columns.size();
    }

    public Column getPrimaryKeyColumn() {
        for (Column column : columns) {
            if (column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }

    public int getPrimaryKeyIndex() {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isPrimaryKey()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Schema{" +
                "columns=" + columns +
                ", foreignKeys=" + foreignKeys +
                '}';
    }
}
