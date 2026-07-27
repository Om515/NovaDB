package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Corresponds to a single database containing multiple tables.
 */
public class Database {
    private String databaseName;
    private Map<String, Table> tables;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tables = new HashMap<>();
    }

    public void addTable(Table table) {
        this.tables.put(table.getTableName(), table);
    }

    public Table getTable(String tableName) {
        return this.tables.get(tableName);
    }

    public boolean containsTable(String tableName) {
        return this.tables.containsKey(tableName);
    }

    public void removeTable(String tableName) {
        this.tables.remove(tableName);
    }

    public Map<String, Table> getTables() {
        return tables;
    }

    @Override
    public String toString() {
        return "Database{" +
                "databaseName='" + databaseName + '\'' +
                ", tables=" + tables.keySet() +
                '}';
    }
}
