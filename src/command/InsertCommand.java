package command;

import java.util.List;

/**
 * Command representing an INSERT operation.
 */
public class InsertCommand extends Command {
    private String tableName;
    private List<Object> values;

    public InsertCommand(String tableName, List<Object> values) {
        super(CommandType.INSERT);
        this.tableName = tableName;
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Object> getValues() {
        return values;
    }
    
    @Override
    public String toString() {
        return "InsertCommand{tableName='" + tableName + "', values=" + values + "}";
    }
}
