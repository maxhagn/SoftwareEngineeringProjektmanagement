package at.ac.tuwien.sepm.groupphase.backend.exception;

public class CreateFailedException extends RuntimeException{
    public CreateFailedException(String message){
        super(message);
    }
}
