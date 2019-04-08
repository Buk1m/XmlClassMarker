package exceptions;

public class FxmlNotFoundException extends RuntimeException {

    public FxmlNotFoundException(String message) {
        super(message + " not found");
    }

    public FxmlNotFoundException(String message, Throwable cause) {
        super(message + " not found", cause);
    }
}
