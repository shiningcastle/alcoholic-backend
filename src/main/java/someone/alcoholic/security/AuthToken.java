package someone.alcoholic.security;


import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import someone.alcoholic.domain.member.Member;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class AuthToken {
    private final String token;
    private final Key key;

    public static final String AUTHORITIES_KEY = "role";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String MEMBER_ID = "member_id";
    public static final String REFRESH_TOKEN_ID = "token_id";
    public static final String NICKNAME_TOKEN = "nickname_token";


    public AuthToken(String token, Key key) {
        this.token = token;
        this.key = key;
    }
    public AuthToken(String memberId, String subject, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(memberId, subject, expiry);
    }
    public AuthToken(UUID tokenId, String subject, String memberId, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(tokenId, subject, memberId, expiry);
    }
    public AuthToken(String memberId, String subject, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(memberId, subject, role, expiry);
    }


    public String createAuthToken(String memberId, String subject, Date expiry) {
        return Jwts.builder()
                .setSubject(subject)
                .claim(MEMBER_ID, memberId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    private String createAuthToken(String memberId, String subject, String role, Date expiry) {
        return Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_KEY, role)
                .claim(MEMBER_ID, memberId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    private String createAuthToken(UUID tokenId, String subject, String memberId, Date expiry) {
        return Jwts.builder()
                .setSubject(subject)
                .claim(MEMBER_ID, memberId)
                .claim(REFRESH_TOKEN_ID, tokenId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    public boolean isValid() {
        return this.getTokenClaims() != null;
    }
    public boolean isExpired() {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return true;
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Authentication getAuthentication() {
        if(isValid()) {
            Claims tokenClaims = this.getTokenClaims();
            List<SimpleGrantedAuthority> authorities = Arrays.stream(new String[]{tokenClaims.get(AUTHORITIES_KEY).toString()})
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            log.debug("claims subject := [{}]", tokenClaims.getSubject());
            User user = new User(tokenClaims.get(MEMBER_ID, String.class), "", authorities);
            return new UsernamePasswordAuthenticationToken(user, this, authorities);

        } else {
            throw new RuntimeException("유효하지 않은 token");
        }

    }
}
