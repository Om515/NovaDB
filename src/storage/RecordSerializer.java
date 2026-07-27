package storage;

import exception.StorageException;
import model.Cell;
import model.Record;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Serializes database records into a custom raw byte format for storage.
 */
public class RecordSerializer {

    /**
     * Serializes a Record object into a byte array manually without using Java Serializable.
     */
    public byte[] serialize(Record record) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            List<Cell> cells = record.getCells();
            dos.writeInt(cells.size());

            for (Cell cell : cells) {
                Object value = cell.getValue();

                if (value instanceof Integer) {
                    dos.writeByte(1);
                    dos.writeInt((Integer) value);
                } else if (value instanceof String) {
                    dos.writeByte(2);
                    dos.writeUTF((String) value);
                } else if (value instanceof Double) {
                    dos.writeByte(3);
                    dos.writeDouble((Double) value);
                } else if (value instanceof Boolean) {
                    dos.writeByte(4);
                    dos.writeBoolean((Boolean) value);
                } else {
                    throw new StorageException("Unsupported or null data type for serialization.");
                }
            }

            dos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new StorageException("Failed to serialize record: " + e.getMessage());
        }
    }
}
