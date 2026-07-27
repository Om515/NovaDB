package storage;

import exception.StorageException;
import model.Cell;
import model.Record;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Deserializes raw bits from storage into readable database records.
 */
public class RecordDeserializer {

    /**
     * Deserializes a raw byte array back into a fully reconstructed Record object.
     */
    public Record deserialize(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             DataInputStream dis = new DataInputStream(bais)) {

            int numCells = dis.readInt();
            Record record = new Record();

            for (int i = 0; i < numCells; i++) {
                byte typeId = dis.readByte();
                Object value;

                switch (typeId) {
                    case 1:
                        value = dis.readInt();
                        break;
                    case 2:
                        value = dis.readUTF();
                        break;
                    case 3:
                        value = dis.readDouble();
                        break;
                    case 4:
                        value = dis.readBoolean();
                        break;
                    default:
                        throw new StorageException("Unknown data type identifier: " + typeId);
                }

                record.addCell(new Cell(value));
            }

            return record;
        } catch (IOException e) {
            throw new StorageException("Failed to deserialize record: " + e.getMessage());
        }
    }
}
