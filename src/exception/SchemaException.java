package exception;

/**
 * Raised in response to invalid meta descriptions or schema irregularities.
 */
public class SchemaException extends DatabaseException {
    public SchemaException(String message) {
        super(message);
    }
}