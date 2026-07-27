package command;

/**
 * Command representing a DELETE operation.
 */
public class DeleteCommand extends Command {
    private String tableName;
    private String whereColumn;
    private Object whereValue;

    public DeleteCommand(String tableName, String whereColumn, Object whereValue) {
        super(CommandType.DELETE);
        this.tableName = tableName;
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
    }

    public String getTableName() {
        return tableName;
    }

    public String getWhereColumn() {
        return whereColumn;
    }

    public Object getWhereValue() {
        return whereValue;
    }
    
    @Override
    public String toString() {
        return "DeleteCommand{tableName='" + tableName + "', whereColumn='" + whereColumn + "', whereValue=" + whereValue + "}";
    }
}
