package storage;

import model.Record;
import model.Table;
import java.util.List;

/**
 * Interface abstracting all low-level storage operations for the engine.
 */
public interface StorageManager {
    void createTable(Table table);
    void dropTable(String tableName);
    boolean tableExists(String tableName);
    int insertRecord(String tableName, Record record);
    List<Record> getRecords(String tableName);
    Record getRecord(String tableName, int recordPosition);
    void updateRecords(String tableName, List<Record> records);
    void deleteRecords(String tableName, List<Record> records);
}
