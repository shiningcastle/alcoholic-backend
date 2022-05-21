package someone.alcoholic.exception;


import org.springframework.security.core.AuthenticationException;

public class BadProviderException extends AuthenticationException {
    private String msg;

    public BadProviderException(String msg) {
        super(msg);
    }
}
