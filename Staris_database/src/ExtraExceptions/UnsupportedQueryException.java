package ExtraExceptions;

/**
 * Exception created for when the user tries to enter a query value that is not
 * supported
 */
public class UnsupportedQueryException extends RuntimeException {
    public UnsupportedQueryException(String message) {
        super(message);
    }
}
