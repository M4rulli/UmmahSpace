package engclasses.exceptions;

public class GeolocalizzazioneFallitaException extends Exception {
    public GeolocalizzazioneFallitaException(String message) {
        super(message);
    }

    public GeolocalizzazioneFallitaException(String message, Throwable cause) {
        super(message, cause);
    }
}