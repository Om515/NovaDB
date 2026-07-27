package engine;

import command.Command;
import exception.DatabaseException;
import index.Index;
import index.IndexManager;
import model.Database;
import parser.SQLParser;
import schema.SchemaManager;
import storage.DatabaseLoader;
import storage.FileStorageManager;
import storage.IndexMetadataManager;
import storage.MetadataManager;
import storage.StorageManager;

import java.util.List;

/**
 * The main database engine facade orchestrating core capabilities and startup recovery sequences.
 */
public class DatabaseEngine {
    private Database database;
    private SQLParser parser;
    private SchemaManager schemaManager;
    private StorageManager storageManager;
    private IndexManager indexManager;
    private QueryEngine queryEngine;

    public DatabaseEngine(String dbName) {
        MetadataManager metadataManager = new MetadataManager();
        DatabaseLoader loader = new DatabaseLoader(metadataManager);
        this.database = loader.loadDatabase(dbName);
        
        this.parser = new SQLParser();
        this.schemaManager = new SchemaManager(database);
        this.storageManager = new FileStorageManager();
        this.indexManager = new IndexManager();
        
        IndexMetadataManager indexMetadataManager = new IndexMetadataManager();
        List<Index> loadedIndexes = indexMetadataManager.loadAllIndexes();
        
        for (Index index : loadedIndexes) {
            indexManager.createIndex(index.getIndexName(), index.getTableName(), index.getColumnName());
            
            Index activeIndex = indexManager.getIndex(index.getIndexName());
            model.Table table = schemaManager.getTable(index.getTableName());
            
            if (table != null) {
                schema.Schema schema = table.getSchema();
                int colIdx = getColumnIndex(schema, index.getColumnName());
                if (colIdx != -1) {
                    List<model.Record> records = storageManager.getRecords(index.getTableName());
                    for (int i = 0; i < records.size(); i++) {
                        model.Record record = records.get(i);
                        Comparable key = (Comparable) record.getCell(colIdx).getValue();
                        indexManager.insertKey(activeIndex, key, i);
                    }
                }
            }
        }
        
        this.queryEngine = new QueryEngine(schemaManager, storageManager, indexManager, indexMetadataManager);
    }

    private int getColumnIndex(schema.Schema schema, String columnName) {
        List<schema.Column> columns = schema.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Executes the given SQL string synchronously start-to-finish.
     */
    public QueryResult execute(String sql) {
        try {
            Command command = parser.parse(sql);
            return queryEngine.execute(command);
        } catch (DatabaseException e) {
            return new QueryResult(false, "Error: " + e.getMessage());
        } catch (Exception e) {
            return new QueryResult(false, "Unexpected error: " + e.getMessage());
        }
    }
}
