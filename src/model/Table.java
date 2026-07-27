package model;

import schema.Schema;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a table entity in the database containing records.
 */
public class Table {
    private String tableName;
    private Schema schema;
    private List<Record> records;

    public Table(String tableName, Schema schema) {
        this.tableName = tableName;
        this.schema = schema;
        this.records = new ArrayList<>();
    }

    public void addRecord(Record record) {
        this.records.add(record);
    }

    public List<Record> getRecords() {
        return records;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableName='" + tableName + '\'' +
                ", schema=" + schema +
                ", recordsSize=" + records.size() +
                '}';
    }
}
