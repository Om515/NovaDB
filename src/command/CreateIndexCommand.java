package command;

public class CreateIndexCommand extends Command {
    private String indexName;
    private String tableName;
    private String columnName;

    public CreateIndexCommand(String indexName, String tableName, String columnName) {
        super(CommandType.CREATE_INDEX);
        this.indexName = indexName;
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }
}
