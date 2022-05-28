package someone.alcoholic.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.enums.ExpiryTime;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.service.token.RefreshTokenService;
import someone.alcoholic.util.CookieUtil;
import someone.alcoholic.util.IpUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private static final String REFRESH_TOKEN_ID = "token_id";
    private static final String MEMBER_ID = "member_id";
    private static final String MEMBER_ROLE = "role";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthToken accessToken = getAuthToken(request, AuthToken.ACCESS_TOKEN);
        String accessTokenValue = accessToken.getToken();
        String accessIp = IpUtil.getClientIP(request);

        if (accessToken != null && accessToken.isValid()) {
            log.info("{} - accessToken is valid", accessIp);
            Authentication authentication = accessToken.getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication : {}", authentication);
        } else if (accessToken != null && !accessToken.isValid()) {
            if (accessToken.isExpired()) {
                AuthToken refreshToken = getAuthToken(request, AuthToken.REFRESH_TOKEN);
                UUID tokenId = refreshToken.getTokenClaims().get(REFRESH_TOKEN_ID, UUID.class);
                RefreshToken savedRefreshToken = refreshTokenService.findById(tokenId);
                String refreshTokenValue = refreshToken.getToken();
                String savedRefreshTokenValue = savedRefreshToken.getTokenValue();
                if (refreshTokenValue != null && refreshTokenValue.equals(savedRefreshTokenValue)) {
                    String savedAccessTokenValue = savedRefreshToken.getAccessToken();
                    if (accessTokenValue.equals(savedAccessTokenValue)) {
                        log.info("{} - accessToken is expired", accessIp);
                        reissue(response, tokenId, savedRefreshToken, refreshToken);
                    } else {
                        refreshTokenService.delete(tokenId);
                        log.info("{} - expires all refreshTokens : {}", accessIp, savedRefreshTokenValue);
                    }
                } else {
                    log.info("{} - refreshToken is different", accessIp);
                }
            } else {
                log.info("{} - accessToken is inValid", accessIp);
            }
        } else {
            log.info("{} - accessToken is null", accessIp);
        }
        filterChain.doFilter(request, response);
    }

    private AuthToken getAuthToken(HttpServletRequest request, String stringToken) {
        AuthToken authToken = null;
        Optional<Cookie> accessTokenCookie = CookieUtil.getCookie(request, stringToken);
        if (accessTokenCookie.isPresent()) {
            String accessTokenStr = accessTokenCookie.get().getValue();
            authToken = tokenProvider.convertAuthToken(accessTokenStr);
        }
        return authToken;
    }

    private void reissue(HttpServletResponse response, UUID uuid, RefreshToken savedRefreshToken, AuthToken refreshToken) {
        String memberId = refreshToken.getTokenClaims().get(AuthToken.MEMBER_ID, String.class);
        String role = refreshToken.getTokenClaims().get(AuthToken.MEMBER_ROLE, String.class);
        AuthToken newAccessToken = tokenProvider.createAccessToken(memberId, role);
        savedRefreshToken.setAccessToken(newAccessToken.getToken());
        refreshTokenService.save(uuid, savedRefreshToken);
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, newAccessToken.getToken(),
                ExpiryTime.ACCESS_COOKIE_EXPIRY_TIME);
        log.info("access token is reissued");
    }
}
