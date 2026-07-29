package storage;

import exception.StorageException;
import model.Table;
import schema.Column;
import schema.DataType;
import schema.Schema;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible strictly for managing the lifecycle of table metadata files on disk.
 */
public class MetadataManager {
    
    private static final String METADATA_DIR = "database/metadata";
    
    public MetadataManager() {
        new File(METADATA_DIR).mkdirs();
    }
    
    private File getMetadataFile(String tableName) {
        return new File(METADATA_DIR, tableName + ".meta");
    }

    /**
     * Serializes a table's schema into a simple text-based format on disk.
     */
    public void saveSchema(Table table) {
        File metaFile = getMetadataFile(table.getTableName());
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(metaFile))) {
            bw.write("TABLE:" + table.getTableName());
            bw.newLine();
            
            for (Column column : table.getSchema().getColumns()) {
                bw.write(column.getName() + ":" + column.getType().name() + ":" + column.isPrimaryKey());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new StorageException("Failed to save metadata for " + table.getTableName() + ": " + e.getMessage());
        }
    }

    /**
     * Reads and parses a .meta file back into a full runtime Schema layout.
     */
    public Schema loadSchema(String tableName) {
        File metaFile = getMetadataFile(tableName);
        if (!metaFile.exists()) {
            throw new StorageException("Metadata file missing for table: " + tableName);
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(metaFile))) {
            String header = br.readLine();
            if (header == null || !header.startsWith("TABLE:")) {
                throw new StorageException("Invalid metadata format for table: " + tableName);
            }
            
            Schema schema = new Schema();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(":");
                if (parts.length < 2 || parts.length > 3) {
                    throw new StorageException("Invalid column format in metadata for: " + tableName);
                }
                
                String colName = parts[0];
                DataType type = DataType.valueOf(parts[1]);
                boolean isPrimaryKey = false;
                if (parts.length == 3) {
                    isPrimaryKey = Boolean.parseBoolean(parts[2]);
                }
                schema.addColumn(new Column(colName, type, isPrimaryKey));
            }
            return schema;
        } catch (IOException e) {
            throw new StorageException("Failed to load metadata for " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Reads directly from the metadata directory and deserializes all available tables.
     */
    public List<Table> loadAllSchemas() {
        List<Table> tables = new ArrayList<>();
        File dir = new File(METADATA_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".meta"));
        
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String tableName = fileName.substring(0, fileName.length() - 5);
                Schema schema = loadSchema(tableName);
                tables.add(new Table(tableName, schema));
            }
        }
        return tables;
    }

    /**
     * Deletes the metadata file associated with a table.
     */
    public void deleteSchema(String tableName) {
        File metaFile = getMetadataFile(tableName);
        if (metaFile.exists()) {
            metaFile.delete();
        }
    }

    /**
     * Checks if a metadata file exists for the given table.
     */
    public boolean schemaExists(String tableName) {
        return getMetadataFile(tableName).exists();
    }
}
