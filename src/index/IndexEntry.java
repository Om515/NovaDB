package index;

/**
 * Represents a key-value mapping within the index.
 */
public class IndexEntry {
    private Object key;
    private int recordPosition;

    public IndexEntry(Object key, int recordPosition) {
        this.key = key;
        this.recordPosition = recordPosition;
    }

    public Object getKey() {
        return key;
    }

    public int getRecordPosition() {
        return recordPosition;
    }
}
