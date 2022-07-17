package someone.alcoholic.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.enums.CookieExpiryTime;
import someone.alcoholic.enums.TokenExpiryTime;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthTokenProvider tokenProvider;

    @Override
    public void save(UUID uuid,  RefreshToken refreshToken) {
        redisTemplate.opsForValue().set(
                uuid.toString(), refreshToken,
                Duration.ofHours(TokenExpiryTime.REDIS_REFRESH_TOKEN_MAX_AGE.getValue()));
    }

    @Override
    public RefreshToken findById(UUID uuid) {
        return (RefreshToken) redisTemplate.opsForValue().get(uuid.toString());
    }

    @Override
    public void delete(UUID uuid) {
        redisTemplate.delete(uuid.toString());
    }

    public AuthToken getAuthToken(HttpServletRequest request, String stringToken) {
        AuthToken authToken = null;
        Optional<Cookie> accessTokenCookie = CookieUtil.getCookie(request, stringToken);
        if (accessTokenCookie.isPresent()) {
            String accessTokenStr = accessTokenCookie.get().getValue();
            authToken = tokenProvider.convertAuthToken(accessTokenStr);
        }
        return authToken;
    }

    public void reissue(HttpServletResponse response, UUID uuid, RefreshToken savedRefreshToken, AuthToken refreshToken) {
        String memberId = refreshToken.getTokenClaims().get(AuthToken.MEMBER_ID, String.class);
        String role = refreshToken.getTokenClaims().get(AuthToken.MEMBER_ROLE, String.class);
        AuthToken newAccessToken = tokenProvider.createAccessToken(memberId, role);
        savedRefreshToken.setAccessToken(newAccessToken.getToken());
        save(uuid, savedRefreshToken);
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, newAccessToken.getToken(),
                CookieExpiryTime.ACCESS_COOKIE_MAX_AGE);
        log.info("access token is reissued");
    }

    public String getMemberIdByAccessToken(HttpServletRequest request) {
        AuthToken accessToken = getAuthToken(request, AuthToken.ACCESS_TOKEN);
        String memberId = null;
        if (accessToken != null) {
            memberId = accessToken.getTokenClaims().get(AuthToken.MEMBER_ID, String.class);
        }
        return memberId;
    }

}
