package index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the global lifecycle of column indices natively.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IndexManager {
    private Map<String, Index> indices;

    public IndexManager() {
        this.indices = new HashMap<>();
    }

    public void createIndex(String indexName, String tableName, String columnName) {
        if (!indices.containsKey(indexName)) {
            indices.put(indexName, new Index(indexName, tableName, columnName));
        }
    }

    public void dropIndex(String indexName) {
        indices.remove(indexName);
    }

    public boolean hasIndex(String indexName) {
        return indices.containsKey(indexName);
    }

    public Index getIndex(String indexName) {
        return indices.get(indexName);
    }
    
    public List<Index> getIndicesForTable(String tableName) {
        List<Index> result = new ArrayList<>();
        for (Index idx : indices.values()) {
            if (idx.getTableName().equals(tableName)) {
                result.add(idx);
            }
        }
        return result;
    }

    public void insertKey(Index index, Comparable key, int recordPosition) {
        index.getTree().insert(key, recordPosition);
    }

    public void deleteKey(Index index, Comparable key) {
        index.getTree().delete(key);
    }

    public void updateKey(Index index, Comparable oldKey, Comparable newKey, int recordPosition) {
        index.getTree().delete(oldKey);
        index.getTree().insert(newKey, recordPosition);
    }

    public Integer searchKey(Index index, Comparable key) {
        Object result = index.getTree().search(key);
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return null;
    }
}
