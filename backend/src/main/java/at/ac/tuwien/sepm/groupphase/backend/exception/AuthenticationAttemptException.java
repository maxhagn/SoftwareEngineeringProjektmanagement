package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserLoginDto;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationAttemptException extends AuthenticationException {
    private final UserLoginDto userData;

    public AuthenticationAttemptException(UserLoginDto userData, String msg, Throwable t) {
        super(msg, t);
        this.userData = userData;
    }

    public AuthenticationAttemptException(UserLoginDto userData, String msg) {
        super(msg);
        this.userData = userData;
    }

    public UserLoginDto getUserData() {
        return userData;
    }
}
