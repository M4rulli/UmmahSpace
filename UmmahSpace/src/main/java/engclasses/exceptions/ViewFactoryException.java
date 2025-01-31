package engclasses.exceptions;

public class ViewFactoryException extends Exception {
    public ViewFactoryException(String message) {
        super(message);
    }

    public ViewFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}