package command;

/**
 * Command representing an UPDATE operation.
 */
public class UpdateCommand extends Command {
    private String tableName;
    private String columnName;
    private Object newValue;
    private String whereColumn;
    private Object whereValue;

    public UpdateCommand(String tableName, String columnName, Object newValue, String whereColumn, Object whereValue) {
        super(CommandType.UPDATE);
        this.tableName = tableName;
        this.columnName = columnName;
        this.newValue = newValue;
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public String getWhereColumn() {
        return whereColumn;
    }

    public Object getWhereValue() {
        return whereValue;
    }
    
    @Override
    public String toString() {
        return "UpdateCommand{tableName='" + tableName + "', columnName='" + columnName + "', newValue=" + newValue + ", whereColumn='" + whereColumn + "', whereValue=" + whereValue + "}";
    }
}
