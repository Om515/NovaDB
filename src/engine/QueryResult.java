package engine;

import model.Record;
import java.util.List;

/**
 * Represents the result of a database query.
 */
public class QueryResult {
    private boolean success;
    private String message;
    private List<Record> records;

    public QueryResult(boolean success, String message, List<Record> records) {
        this.success = success;
        this.message = message;
        this.records = records;
    }

    public QueryResult(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Record> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                (records != null ? ", records=" + records : "") +
                '}';
    }
}
