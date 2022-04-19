package at.ac.tuwien.sepm.groupphase.backend.exception;


public class NotEnoughSeatsException extends RuntimeException {

    public NotEnoughSeatsException() {
    }

    public NotEnoughSeatsException(String message) {
        super(message);
    }

    public NotEnoughSeatsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughSeatsException(Exception e) {
        super(e);
    }
}
