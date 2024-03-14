package ExtraExceptions;

/**
 * Exception for when the user tries to connect to an existing database, but
 * enters an incorrect path
 */
public class IncorrectDataBasePathException extends RuntimeException {
    public IncorrectDataBasePathException(String message) {
        super(message);
    }

}
