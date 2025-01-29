package engclasses.exceptions;

public class HttpRequestException extends Exception {
    public HttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}