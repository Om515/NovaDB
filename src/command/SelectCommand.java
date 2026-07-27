package command;

/**
 * Command representing a SELECT operation.
 */
public class SelectCommand extends Command {
    private String tableName;
    private String whereColumn;
    private Object whereValue;
    private boolean selectAll;

    public SelectCommand(String tableName, boolean selectAll, String whereColumn, Object whereValue) {
        super(CommandType.SELECT);
        this.tableName = tableName;
        this.selectAll = selectAll;
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public String getWhereColumn() {
        return whereColumn;
    }

    public Object getWhereValue() {
        return whereValue;
    }
    
    @Override
    public String toString() {
        return "SelectCommand{tableName='" + tableName + "', selectAll=" + selectAll + ", whereColumn='" + whereColumn + "', whereValue=" + whereValue + "}";
    }
}
