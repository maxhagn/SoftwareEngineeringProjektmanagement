package at.ac.tuwien.sepm.groupphase.backend.exception;


public class ServerErrorException extends RuntimeException {

    public ServerErrorException() {
    }

    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerErrorException(Exception e) {
        super(e);
    }
}
