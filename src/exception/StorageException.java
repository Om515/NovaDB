package exception;

/**
 * Thrown concerning issues inside the storage engine reading or writing data.
 */
public class StorageException extends DatabaseException {
    public StorageException(String message) {
        super(message);
    }
}