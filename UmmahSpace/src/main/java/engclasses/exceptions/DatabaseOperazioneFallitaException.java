package engclasses.exceptions;

public class DatabaseOperazioneFallitaException extends Exception {
    public DatabaseOperazioneFallitaException(String message) {
        super(message);
    }
    public DatabaseOperazioneFallitaException(String message, Throwable cause) {
        super(message, cause);
    }
}