package at.ac.tuwien.sepm.groupphase.backend.exception;


public class SelfLockException extends RuntimeException {

    public SelfLockException() {
    }

    public SelfLockException(String message) {
        super(message);
    }

    public SelfLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelfLockException(Exception e) {
        super(e);
    }
}
