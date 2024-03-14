package ExtraExceptions;

/* Exception when someone tries to perform an incorrect type of search - eg a venue search on a request holding an author search */
public class UnsupportedSearchTypeException extends RuntimeException {
    public UnsupportedSearchTypeException(String message) {
        super(message);
    }

}