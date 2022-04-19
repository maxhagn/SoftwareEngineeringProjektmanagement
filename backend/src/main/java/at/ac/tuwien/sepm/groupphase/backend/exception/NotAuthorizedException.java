package at.ac.tuwien.sepm.groupphase.backend.exception;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String msg) {
        super(msg);
    }
}
