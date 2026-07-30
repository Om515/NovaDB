package schema;

/**
 * Represents a foreign key constraint in a table schema.
 */
public class ForeignKeyConstraint {
    private String childColumn;
    private String parentTable;
    private String parentColumn;

    public ForeignKeyConstraint(String childColumn, String parentTable, String parentColumn) {
        this.childColumn = childColumn;
        this.parentTable = parentTable;
        this.parentColumn = parentColumn;
    }

    public String getChildColumn() {
        return childColumn;
    }

    public String getParentTable() {
        return parentTable;
    }

    public String getParentColumn() {
        return parentColumn;
    }

    @Override
    public String toString() {
        return "ForeignKeyConstraint{" +
                "childColumn='" + childColumn + '\'' +
                ", parentTable='" + parentTable + '\'' +
                ", parentColumn='" + parentColumn + '\'' +
                '}';
    }
}
