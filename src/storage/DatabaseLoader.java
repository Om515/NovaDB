package storage;

import model.Database;
import model.Table;
import java.util.List;

/**
 * Responsible for reconstructing the Database catalogue on startup by loading metadata files.
 */
public class DatabaseLoader {

    private MetadataManager metadataManager;

    public DatabaseLoader(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    /**
     * Initializes a new Database or restores an existing one by scanning metadata.
     */
    public Database loadDatabase(String dbName) {
        Database database = new Database(dbName);
        List<Table> tables = metadataManager.loadAllSchemas();
        
        for (Table table : tables) {
            database.addTable(table);
        }
        
        return database;
    }
}
