package someone.alcoholic.security;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.enums.ExpiryTime;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Component
public class AuthTokenProvider {
    private String secret;
    private final Key key;

    public AuthTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(this.secret.getBytes());
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public AuthToken createAccessToken(String memberId, String role) {
        return new AuthToken(memberId, AuthToken.ACCESS_TOKEN, role,
                new Date(new Date().getTime() + ExpiryTime.ACCESS_TOKEN_EXPIRY_TIME.getTime()), key);
    }

    public AuthToken createRefreshToken(UUID tokenId, String memberId) {
        return new AuthToken(tokenId, AuthToken.REFRESH_TOKEN, memberId,
                new Date(new Date().getTime() + ExpiryTime.REFRESH_TOKEN_EXPIRY_TIME.getTime()), key);
    }
    public AuthToken createNicknameToken(String memberId) {
        return new AuthToken(memberId, AuthToken.NICKNAME_TOKEN,
                new Date(new Date().getTime() + ExpiryTime.NICKNAME_TOKEN_EXPIRY_TIME.getTime()), key);
    }
}
