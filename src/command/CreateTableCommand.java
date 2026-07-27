package command;

import schema.Schema;

/**
 * Command representing a CREATE TABLE operation.
 */
public class CreateTableCommand extends Command {
    private String tableName;
    private Schema schema;

    public CreateTableCommand(String tableName, Schema schema) {
        super(CommandType.CREATE_TABLE);
        this.tableName = tableName;
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public Schema getSchema() {
        return schema;
    }
    
    @Override
    public String toString() {
        return "CreateTableCommand{tableName='" + tableName + "', schema=" + schema + "}";
    }
}
