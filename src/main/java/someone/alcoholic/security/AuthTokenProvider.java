package someone.alcoholic.security;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.enums.Age;

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
        return new AuthToken(memberId, role, new Date(new Date().getTime() + Age.ACCESS_TOKEN_MAX_AGE.getTime()), key);
    }

    public AuthToken createRefreshToken(String memberId) {
        return new AuthToken(memberId, new Date(new Date().getTime() + Age.REFRESH_TOKEN_MAX_AGE.getTime()), key);
    }

    public AuthToken createRefreshToken(UUID tokenId, String memberId) {
        return new AuthToken(tokenId, memberId, new Date(new Date().getTime() + Age.REFRESH_TOKEN_MAX_AGE.getTime()), key);
    }
    public AuthToken createNicknameToken(Member member) {
        return new AuthToken(member, new Date(new Date().getTime() + Age.NICKNAME_TOKEN_MAX_AGE.getTime()), key);
    }
}
