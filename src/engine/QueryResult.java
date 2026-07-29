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

    private List<String> columnNames;

    public QueryResult(boolean success, String message, List<Record> records, List<String> columnNames) {
        this.success = success;
        this.message = message;
        this.records = records;
        this.columnNames = columnNames;
    }

    public QueryResult(boolean success, String message, List<Record> records) {
        this(success, message, records, null);
    }

    public QueryResult(boolean success, String message) {
        this(success, message, null, null);
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
        if (records == null) {
            return message;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(message).append("\n");

        if (columnNames != null && !columnNames.isEmpty()) {
            int numCols = columnNames.size();
            int[] colWidths = new int[numCols];

            for (int i = 0; i < numCols; i++) {
                colWidths[i] = columnNames.get(i).length();
            }
            for (Record record : records) {
                for (int i = 0; i < numCols; i++) {
                    String val = String.valueOf(record.getCell(i).getValue());
                    if (val.length() > colWidths[i]) {
                        colWidths[i] = val.length();
                    }
                }
            }

            String separator = buildSeparator(colWidths);
            sb.append(separator).append("\n");

            sb.append("|");
            for (int i = 0; i < numCols; i++) {
                sb.append(" ").append(padRight(columnNames.get(i), colWidths[i])).append(" |");
            }
            sb.append("\n").append(separator).append("\n");

            for (Record record : records) {
                sb.append("|");
                for (int i = 0; i < numCols; i++) {
                    String val = String.valueOf(record.getCell(i).getValue());
                    sb.append(" ").append(padRight(val, colWidths[i])).append(" |");
                }
                sb.append("\n");
            }
            sb.append(separator);
        } else {
            sb.append(records.toString());
        }
        
        return sb.toString();
    }

    private String buildSeparator(int[] colWidths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : colWidths) {
            for (int i = 0; i < w + 2; i++) sb.append("-");
            sb.append("+");
        }
        return sb.toString();
    }

    private String padRight(String s, int n) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) sb.append(" ");
        return sb.toString();
    }
}
