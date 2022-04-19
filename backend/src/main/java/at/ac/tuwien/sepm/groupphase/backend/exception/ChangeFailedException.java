package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ChangeFailedException extends RuntimeException{
    public ChangeFailedException(String message){
        super(message);
    }
}
