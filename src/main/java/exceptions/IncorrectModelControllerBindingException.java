package exceptions;

public class IncorrectModelControllerBindingException extends RuntimeException {
    public IncorrectModelControllerBindingException() {
    }

    public IncorrectModelControllerBindingException(String message) {
        super(message);
    }

    public IncorrectModelControllerBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectModelControllerBindingException(Throwable cause) {
        super(cause);
    }
}
