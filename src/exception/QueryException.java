package exception;

/**
 * Raised whenever the engine cannot correctly execute a query.
 */
public class QueryException extends DatabaseException {

    public QueryException(String message) {
        super(message);
    }

}