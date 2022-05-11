package someone.alcoholic.security;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Component
public class AuthTokenProvider {
    private String secret;
    @Value("${jwt.access-token-max-age}")
    private int accessTokenMaxAge;
    @Value("${jwt.refresh-token-max-age}")
    private int refreshTokenMaxAge;
    private final Key key;

    public AuthTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(this.secret.getBytes());
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public AuthToken createAccessToken(String memberId, String role, Date expiry) {
        return new AuthToken(memberId, role, expiry, key);
    }

    public AuthToken createRefreshToken(String memberId, Date expiry) {
        return new AuthToken(memberId, expiry, key);
    }

    public AuthToken createRefreshToken(UUID tokenId, String memberId, Date expiry) {
        return new AuthToken(tokenId, memberId, expiry, key);
    }
}
