package index;

/**
 * Encapsulates an index mapped to a specific table column.
 */
@SuppressWarnings("rawtypes")
public class Index {
    private String indexName;
    private String tableName;
    private String columnName;
    private BPlusTree tree;

    public Index(String indexName, String tableName, String columnName) {
        this.indexName = indexName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.tree = new BPlusTree();
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

    public BPlusTree getTree() {
        return tree;
    }
}
