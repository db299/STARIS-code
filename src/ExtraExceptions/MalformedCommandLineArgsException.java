package ExtraExceptions;

/* Exception for when the user gives the program an invalid input */
public class MalformedCommandLineArgsException extends RuntimeException {
    public MalformedCommandLineArgsException(String message) {
        super(message);
    }

}
