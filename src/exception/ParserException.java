package exception;

/**
 * Thrown during errors within the SQL parsing or tokenizing steps.
 */
public class ParserException extends DatabaseException {
    public ParserException() {
        super("Parser Exception");
    }
    
    public ParserException(String message) {
        super(message);
    }
}
