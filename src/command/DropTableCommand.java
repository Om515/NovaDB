package command;

/**
 * Command representing a DROP TABLE operation.
 */
public class DropTableCommand extends Command {
    private String tableName;

    public DropTableCommand(String tableName) {
        super(CommandType.DROP_TABLE);
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
    
    @Override
    public String toString() {
        return "DropTableCommand{tableName='" + tableName + "'}";
    }
}
