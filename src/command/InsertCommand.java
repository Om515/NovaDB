package command;

import java.util.List;

/**
 * Command representing an INSERT operation.
 */
public class InsertCommand extends Command {
    private String tableName;
    private List<List<Object>> valuesList;

    public InsertCommand(String tableName, List<List<Object>> valuesList) {
        super(CommandType.INSERT);
        this.tableName = tableName;
        this.valuesList = valuesList;
    }

    public String getTableName() {
        return tableName;
    }

    public List<List<Object>> getValuesList() {
        return valuesList;
    }
    
    @Override
    public String toString() {
        return "InsertCommand{tableName='" + tableName + "', valuesList=" + valuesList + "}";
    }
}
