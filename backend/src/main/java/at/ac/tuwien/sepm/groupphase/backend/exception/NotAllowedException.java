package at.ac.tuwien.sepm.groupphase.backend.exception;


public class NotAllowedException extends RuntimeException {

    public NotAllowedException() {
    }

    public NotAllowedException(String message) {
        super(message);
    }

    public NotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAllowedException(Exception e) {
        super(e);
    }
}
