package someone.alcoholic.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.enums.Age;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.service.token.RefreshTokenService;
import someone.alcoholic.util.CookieUtil;

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
    private static final String MEMBER_ID = "token_id";
    private static final String MEMBER_ROLE = "role";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthToken accessToken;
        AuthToken refreshToken;

        accessToken = getAuthToken(request, AuthToken.ACCESS_TOKEN);
        refreshToken = getAuthToken(request, AuthToken.REFRESH_TOKEN);

        if (accessToken == null) {
            log.info("accessToken 없음");
            filterChain.doFilter(request, response);
            return;
        } else if (accessToken.isValid()) {
            log.info("accessToken이 유효함");
            Authentication authentication = accessToken.getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (accessToken.isExpired() && refreshToken!=null && refreshToken.isValid()) {
            log.info("accessToken이 만료되었고 refreshToken은 사용 가능함");
            reissue(request, response, refreshToken);
        } else {
            log.info("accessToken과 refreshToken이 유효하지 않음");
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

    private void reissue(HttpServletRequest request, HttpServletResponse response, AuthToken refreshToken) {
        UUID refreshTokenPK = refreshToken.getTokenClaims().get(REFRESH_TOKEN_ID, UUID.class);
        String memberId = refreshToken.getTokenClaims().get(MEMBER_ID, String.class);
        String role = refreshToken.getTokenClaims().get(MEMBER_ROLE, String.class);

        RefreshToken savedRefreshToken = refreshTokenService
                .findByIdAndMemberId(refreshTokenPK, memberId).orElse(null);
        if (savedRefreshToken == null) {
            log.info("refresh token이 db에 저장되어있지 않다.");
            CookieUtil.deleteCookie(request, response, AuthToken.REFRESH_TOKEN);
            CookieUtil.deleteCookie(request, response, AuthToken.ACCESS_TOKEN);
        } else if (refreshToken.getToken().equals(savedRefreshToken.getRefreshToken())) {
            log.info("access, refresh token이 재발급 되었다.");
            AuthToken newAccessToken= tokenProvider.createAccessToken(memberId, role);
            UUID newRefreshTokenPK = UUID.randomUUID();
            AuthToken newRefreshToken = tokenProvider.createRefreshToken(newRefreshTokenPK, memberId);
            refreshTokenService.save(
                    new RefreshToken(newRefreshTokenPK, memberId, newRefreshToken.getToken()));
            setCookie(response, newAccessToken, newRefreshToken);
        }
    }

    private void setCookie(HttpServletResponse response, AuthToken newAccessToken, AuthToken newRefreshToken) {
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, newAccessToken.getToken(),
                Age.ACCESS_COOKIE_MAX_AGE);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, newRefreshToken.getToken(),
                Age.REFRESH_COOKIE_MAX_AGE);
    }
}
