package ExtraExceptions;
/**
 * Exception thrown when something goes wrong in initialising the database
 */
public class DataBaseInitialisationException extends RuntimeException {
    public DataBaseInitialisationException(String message) {
        super(message);
    }
}
