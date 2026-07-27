package schema;

import command.CreateTableCommand;
import exception.SchemaException;
import model.Database;
import model.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages schemas, table configurations, and metadata across the catalog (Database).
 */
public class SchemaManager {
    private Database catalog;

    public SchemaManager(Database catalog) {
        this.catalog = catalog;
    }

    /**
     * Creates a new table based on the provided CreateTableCommand.
     * Throws SchemaException if the table already exists or if there are duplicate columns.
     */
    public void createTable(CreateTableCommand command) {
        String tableName = command.getTableName();
        
        if (catalog.containsTable(tableName)) {
            throw new SchemaException("Table '" + tableName + "' already exists.");
        }

        Schema schema = command.getSchema();
        Set<String> columnNames = new HashSet<>();
        
        for (Column col : schema.getColumns()) {
            if (!columnNames.add(col.getName())) {
                throw new SchemaException("Duplicate column name '" + col.getName() + "' found in table definition.");
            }
        }

        Table table = new Table(tableName, schema);
        catalog.addTable(table);
    }

    /**
     * Drops the specified table.
     * Throws SchemaException if the table does not exist.
     */
    public void dropTable(String tableName) {
        if (!catalog.containsTable(tableName)) {
            throw new SchemaException("Table '" + tableName + "' does not exist.");
        }
        catalog.removeTable(tableName);
    }

    /**
     * Checks whether a table exists in the catalog.
     */
    public boolean containsTable(String tableName) {
        return catalog.containsTable(tableName);
    }

    /**
     * Retrieves the specified table object.
     * Throws SchemaException if the table is not found.
     */
    public Table getTable(String tableName) {
        if (!catalog.containsTable(tableName)) {
            throw new SchemaException("Table '" + tableName + "' does not exist.");
        }
        return catalog.getTable(tableName);
    }

    /**
     * Returns a list of all table names currently present in the catalog.
     */
    public List<String> showTables() {
        return new ArrayList<>(catalog.getTables().keySet());
    }
}
