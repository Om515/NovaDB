package storage;

import exception.StorageException;
import index.Index;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the persistence and retrieval of abstract Index structural definitions on disk.
 */
public class IndexMetadataManager {

    private static final String INDEXES_DIR = "database/indexes";

    public IndexMetadataManager() {
        new File(INDEXES_DIR).mkdirs();
    }

    public void saveIndex(Index index) {
        File file = new File(INDEXES_DIR, index.getIndexName() + ".meta");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("INDEX:" + index.getIndexName());
            writer.println("TABLE:" + index.getTableName());
            writer.println("COLUMN:" + index.getColumnName());
        } catch (IOException e) {
            throw new StorageException("Failed to save index metadata: " + e.getMessage());
        }
    }

    public Index loadIndex(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String indexLine = reader.readLine();
            String tableLine = reader.readLine();
            String columnLine = reader.readLine();
            
            if (indexLine != null && tableLine != null && columnLine != null) {
                String indexName = indexLine.split(":")[1];
                String tableName = tableLine.split(":")[1];
                String columnName = columnLine.split(":")[1];
                return new Index(indexName, tableName, columnName);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to load index metadata: " + e.getMessage());
        }
        return null;
    }

    public List<Index> loadAllIndexes() {
        List<Index> indexes = new ArrayList<>();
        File dir = new File(INDEXES_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".meta"));
        
        if (files != null) {
            for (File file : files) {
                Index index = loadIndex(file);
                if (index != null) {
                    indexes.add(index);
                }
            }
        }
        return indexes;
    }

    public void deleteIndex(String indexName) {
        File file = new File(INDEXES_DIR, indexName + ".meta");
        if (file.exists()) {
            file.delete();
        }
    }
}
