package someone.alcoholic.exception.oauth;

public class OAuthProviderNotFoundException extends RuntimeException {
    OAuthProviderNotFoundException() {}
    public OAuthProviderNotFoundException(String message) {
        super(message);
    }
}
