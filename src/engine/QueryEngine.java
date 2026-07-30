package engine;

import command.*;
import exception.QueryException;
import index.Index;
import index.IndexManager;
import model.Cell;
import model.Record;
import model.Table;
import optimizer.ExecutionPlan;
import optimizer.QueryOptimizer;
import schema.Column;
import schema.Schema;
import schema.SchemaManager;
import storage.IndexMetadataManager;
import storage.StorageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for fulfilling CRUD Command requests delegating logic through
 * StorageManager and synchronizing IndexManager.
 */
public class QueryEngine {
    private SchemaManager schemaManager;
    private StorageManager storageManager;
    private IndexManager indexManager;
    private IndexMetadataManager indexMetadataManager;
    private QueryOptimizer optimizer;

    public QueryEngine(SchemaManager schemaManager, StorageManager storageManager, IndexManager indexManager, IndexMetadataManager indexMetadataManager) {
        this.schemaManager = schemaManager;
        this.storageManager = storageManager;
        this.indexManager = indexManager;
        this.indexMetadataManager = indexMetadataManager;
        this.optimizer = new QueryOptimizer(indexManager);
    }

    /**
     * Evaluates and routes a parsed Command object payload into actionable DB
     * requests.
     */
    public QueryResult execute(Command command) {
        switch (command.getType()) {
            case CREATE_TABLE:
                return executeCreate((CreateTableCommand) command);
            case INSERT:
                return executeInsert((InsertCommand) command);
            case SELECT:
                return executeSelect((SelectCommand) command);
            case UPDATE:
                return executeUpdate((UpdateCommand) command);
            case DELETE:
                return executeDelete((DeleteCommand) command);
            case SHOW_TABLES:
                return executeShowTables((ShowTablesCommand) command);
            case DROP_TABLE:
                return executeDropTable((DropTableCommand) command);
            case CREATE_INDEX:
                return executeCreateIndex((CreateIndexCommand) command);
            case DROP_INDEX:
                return executeDropIndex((DropIndexCommand) command);
            default:
                throw new QueryException("Unsupported command type: " + command.getType());
        }
    }

    private QueryResult executeCreate(CreateTableCommand cmd) {
        schemaManager.createTable(cmd);
        storageManager.createTable(schemaManager.getTable(cmd.getTableName()));
        return new QueryResult(true, "Table '" + cmd.getTableName() + "' created successfully.");
    }

    private QueryResult executeDropTable(DropTableCommand cmd) {
        schemaManager.dropTable(cmd.getTableName());
        storageManager.dropTable(cmd.getTableName());
        return new QueryResult(true, "Table '" + cmd.getTableName() + "' dropped successfully.");
    }

    private QueryResult executeShowTables(ShowTablesCommand cmd) {
        List<String> tables = schemaManager.showTables();
        return new QueryResult(true, "Tables: " + tables.toString());
    }

    private QueryResult executeCreateIndex(CreateIndexCommand cmd) {
        indexManager.createIndex(cmd.getIndexName(), cmd.getTableName(), cmd.getColumnName());
        
        Index index = indexManager.getIndex(cmd.getIndexName());
        indexMetadataManager.saveIndex(index);
        
        Table table = schemaManager.getTable(cmd.getTableName());
        Schema schema = table.getSchema();
        int colIndex = getColumnIndex(schema, cmd.getColumnName());
        
        List<Record> records = storageManager.getRecords(cmd.getTableName());
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            Comparable key = (Comparable) record.getCell(colIndex).getValue();
            indexManager.insertKey(index, key, i);
        }
        
        return new QueryResult(true, "Index created.");
    }

    private QueryResult executeDropIndex(DropIndexCommand cmd) {
        indexManager.dropIndex(cmd.getIndexName());
        indexMetadataManager.deleteIndex(cmd.getIndexName());
        return new QueryResult(true, "Index dropped.");
    }

    private void checkUniqueConstraint(Schema schema, String tableName, int colIndex, Object value, List<Record> allRecords, boolean isPrimaryKey) {
        Column column = schema.getColumns().get(colIndex);
        
        Index targetIndex = null;
        for (Index idx : indexManager.getIndicesForTable(tableName)) {
            if (idx.getColumnName().equals(column.getName())) {
                targetIndex = idx;
                break;
            }
        }
        
        String errorMsg = isPrimaryKey 
            ? "Duplicate PRIMARY KEY value: " + value 
            : "Duplicate UNIQUE value for column '" + column.getName() + "'.";
            
        if (targetIndex != null) {
            Integer pos = indexManager.searchKey(targetIndex, (Comparable) value);
            if (pos != null) {
                throw new QueryException(errorMsg);
            }
        } else {
            for (Record record : allRecords) {
                if (matchCondition(record, colIndex, value)) {
                    throw new QueryException(errorMsg);
                }
            }
        }
    }

    private void validateForeignKey(Schema schema, int colIndex, Object value) {
        if (value == null) return;
        
        Column column = schema.getColumns().get(colIndex);
        for (schema.ForeignKeyConstraint fk : schema.getForeignKeys()) {
            if (fk.getChildColumn().equals(column.getName())) {
                String parentTableName = fk.getParentTable();
                String parentColName = fk.getParentColumn();
                
                Table parentTable = schemaManager.getTable(parentTableName);
                if (parentTable == null) {
                    throw new QueryException("Foreign key constraint violated.");
                }
                
                Schema parentSchema = parentTable.getSchema();
                int parentColIndex = getColumnIndex(parentSchema, parentColName);
                
                Index targetIndex = null;
                for (Index idx : indexManager.getIndicesForTable(parentTableName)) {
                    if (idx.getColumnName().equals(parentColName)) {
                        targetIndex = idx;
                        break;
                    }
                }
                
                boolean found = false;
                if (targetIndex != null) {
                    Integer pos = indexManager.searchKey(targetIndex, (Comparable) value);
                    if (pos != null) {
                        found = true;
                    }
                } else {
                    for (Record parentRecord : storageManager.getRecords(parentTableName)) {
                        if (matchCondition(parentRecord, parentColIndex, value)) {
                            found = true;
                            break;
                        }
                    }
                }
                
                if (!found) {
                    throw new QueryException("Foreign key constraint violated.");
                }
            }
        }
    }

    private void validateDeleteForeignKeys(String tableName, Record deletedRecord) {
        Table currentTable = schemaManager.getTable(tableName);
        List<String> allTables = schemaManager.showTables();
        
        for (String childTableName : allTables) {
            Table childTable = schemaManager.getTable(childTableName);
            for (schema.ForeignKeyConstraint fk : childTable.getSchema().getForeignKeys()) {
                if (fk.getParentTable().equals(tableName)) {
                    int parentColIndex = getColumnIndex(currentTable.getSchema(), fk.getParentColumn());
                    Object deletedValueForThisFk = deletedRecord.getCell(parentColIndex).getValue();
                    
                    int childColIndex = getColumnIndex(childTable.getSchema(), fk.getChildColumn());
                    
                    Index targetIndex = null;
                    for (Index idx : indexManager.getIndicesForTable(childTableName)) {
                        if (idx.getColumnName().equals(fk.getChildColumn())) {
                            targetIndex = idx;
                            break;
                        }
                    }
                    
                    if (targetIndex != null) {
                        Integer pos = indexManager.searchKey(targetIndex, (Comparable) deletedValueForThisFk);
                        if (pos != null) {
                            throw new QueryException("Foreign key constraint violation.");
                        }
                    } else {
                        for (Record childRecord : storageManager.getRecords(childTableName)) {
                            if (matchCondition(childRecord, childColIndex, deletedValueForThisFk)) {
                                throw new QueryException("Foreign key constraint violation.");
                            }
                        }
                    }
                }
            }
        }
    }

    private QueryResult executeInsert(InsertCommand cmd) {
        Table table = schemaManager.getTable(cmd.getTableName());
        Schema schema = table.getSchema();
        List<List<Object>> valuesList = cmd.getValuesList();

        int insertedRows = 0;

        for (List<Object> values : valuesList) {
            if (values.size() != schema.getColumnCount()) {
                throw new QueryException("Insert value count (" + values.size() +
                        ") does not match schema column count (" + schema.getColumnCount() + ").");
            }

            for (int colIndex = 0; colIndex < schema.getColumnCount(); colIndex++) {
                Column column = schema.getColumns().get(colIndex);
                Object value = values.get(colIndex);
                
                if (column.isPrimaryKey() || column.isNotNull()) {
                    if (value == null) {
                        String errorMsg = column.isPrimaryKey() 
                            ? "PRIMARY KEY cannot be NULL." 
                            : "NOT NULL constraint violated on column '" + column.getName() + "'.";
                        throw new QueryException(errorMsg);
                    }
                }
                
                if (value != null && (column.isPrimaryKey() || column.isUnique())) {
                    checkUniqueConstraint(schema, cmd.getTableName(), colIndex, value, storageManager.getRecords(cmd.getTableName()), column.isPrimaryKey());
                }
                
                validateForeignKey(schema, colIndex, value);
            }

            Record record = new Record();
            for (Object value : values) {
                record.addCell(new Cell(value));
            }

            int recordPosition = storageManager.insertRecord(cmd.getTableName(), record);

            for (Index index : indexManager.getIndicesForTable(cmd.getTableName())) {
                int colIndex = getColumnIndex(schema, index.getColumnName());
                Comparable key = (Comparable) record.getCell(colIndex).getValue();
                System.out.println(
                        "Inserted key=" + key +
                                " position=" + recordPosition);
                indexManager.insertKey(index, key, recordPosition);
            }
            insertedRows++;
        }

        return new QueryResult(true, insertedRows + " row(s) inserted.");
    }

    private QueryResult executeSelect(SelectCommand cmd) {
        Table table = schemaManager.getTable(cmd.getTableName());
        Schema schema = table.getSchema();
        List<Record> results = new ArrayList<>();

        ExecutionPlan plan = optimizer.choosePlan(table, cmd);
        System.out.println("Execution Plan : " + plan.name());

        if (plan == ExecutionPlan.INDEX_SCAN) {
            String colName = cmd.getWhereColumn();
            Comparable searchKey = (Comparable) cmd.getWhereValue();

            Index targetIndex = null;
            for (Index idx : indexManager.getIndicesForTable(cmd.getTableName())) {
                if (idx.getColumnName().equals(colName)) {
                    targetIndex = idx;
                    break;
                }
            }

            Integer position = indexManager.searchKey(targetIndex, searchKey);
            System.out.println(
                    "Searching key=" + searchKey +
                            "\nReturned position=" + position);
            if (position != null) {
                Record r = storageManager.getRecord(cmd.getTableName(), position);
                if (r != null) {
                    results.add(r);
                }
            }
        } else {
            int whereColIndex = -1;
            if (cmd.getWhereColumn() != null) {
                whereColIndex = getColumnIndex(schema, cmd.getWhereColumn());
            }

            for (Record record : storageManager.getRecords(cmd.getTableName())) {
                if (whereColIndex == -1 || matchCondition(record, whereColIndex, cmd.getWhereValue())) {
                    results.add(record);
                }
            }
        }

        List<String> colNames = new ArrayList<>();
        for (Column col : schema.getColumns()) {
            colNames.add(col.getName());
        }

        return new QueryResult(true, results.size() + " rows selected.", results, colNames);
    }

    private QueryResult executeUpdate(UpdateCommand cmd) {
        Table table = schemaManager.getTable(cmd.getTableName());
        Schema schema = table.getSchema();

        int updateColIndex = getColumnIndex(schema, cmd.getColumnName());
        int whereColIndex = getColumnIndex(schema, cmd.getWhereColumn());

        Column updateColumn = schema.getColumns().get(updateColIndex);
        
        if (updateColumn.isPrimaryKey() || updateColumn.isNotNull()) {
            if (cmd.getNewValue() == null) {
                String errorMsg = updateColumn.isPrimaryKey() 
                    ? "PRIMARY KEY cannot be NULL." 
                    : "NOT NULL constraint violated on column '" + updateColumn.getName() + "'.";
                throw new QueryException(errorMsg);
            }
        }

        List<Record> updatedRecords = new ArrayList<>();
        int updatedCount = 0;

        List<Record> allRecords = storageManager.getRecords(cmd.getTableName());
        for (Record record : allRecords) {
            if (matchCondition(record, whereColIndex, cmd.getWhereValue())) {
                if (updateColumn.isPrimaryKey() || updateColumn.isUnique()) {
                    Object oldValue = record.getCell(updateColIndex).getValue();
                    if (!cmd.getNewValue().equals(oldValue)) {
                        if (updatedCount > 0) {
                             String errorMsg = updateColumn.isPrimaryKey() 
                                 ? "Duplicate PRIMARY KEY value: " + cmd.getNewValue() 
                                 : "Duplicate UNIQUE value for column '" + updateColumn.getName() + "'.";
                             throw new QueryException(errorMsg);
                        }
                        checkUniqueConstraint(schema, cmd.getTableName(), updateColIndex, cmd.getNewValue(), allRecords, updateColumn.isPrimaryKey());
                    }
                }
                
                validateForeignKey(schema, updateColIndex, cmd.getNewValue());

                for (Index index : indexManager.getIndicesForTable(cmd.getTableName())) {
                    int colIdx = getColumnIndex(schema, index.getColumnName());
                    Comparable oldKey = (Comparable) record.getCell(colIdx).getValue();
                    Comparable newKey = (colIdx == updateColIndex) ? (Comparable) cmd.getNewValue() : oldKey;

                    if (!oldKey.equals(newKey)) {
                        indexManager.updateKey(index, oldKey, newKey, 0);
                    }
                }

                record.getCell(updateColIndex).setValue(cmd.getNewValue());
                updatedRecords.add(record);
                updatedCount++;
            }
        }

        storageManager.updateRecords(cmd.getTableName(), updatedRecords);
        return new QueryResult(true, updatedCount + " rows updated.");
    }

    private QueryResult executeDelete(DeleteCommand cmd) {
        Table table = schemaManager.getTable(cmd.getTableName());
        Schema schema = table.getSchema();

        int whereColIndex = getColumnIndex(schema, cmd.getWhereColumn());

        List<Record> recordsToDelete = new ArrayList<>();
        int counter = 0;

        for (Record record : storageManager.getRecords(cmd.getTableName())) {
            if (matchCondition(record, whereColIndex, cmd.getWhereValue())) {
                validateDeleteForeignKeys(cmd.getTableName(), record);
                
                recordsToDelete.add(record);
                counter++;

                for (Index index : indexManager.getIndicesForTable(cmd.getTableName())) {
                    int colIdx = getColumnIndex(schema, index.getColumnName());
                    indexManager.deleteKey(index, (Comparable) record.getCell(colIdx).getValue());
                }
            }
        }

        storageManager.deleteRecords(cmd.getTableName(), recordsToDelete);
        return new QueryResult(true, counter + " rows deleted.");
    }

    private int getColumnIndex(Schema schema, String columnName) {
        List<Column> columns = schema.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(columnName)) {
                return i;
            }
        }
        throw new QueryException("Column '" + columnName + "' does not exist.");
    }

    private boolean matchCondition(Record record, int colIndex, Object expectedValue) {
        Object actualValue = record.getCell(colIndex).getValue();
        if (actualValue == null && expectedValue == null)
            return true;
        if (actualValue == null || expectedValue == null)
            return false;
        return actualValue.toString().equals(expectedValue
                .toString());
    }

}
