package someone.alcoholic.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.repository.token.RefreshTokenRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
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
            filterChain.doFilter(request, response);
            return;
        } else if (accessToken.isValid()) {
            Authentication authentication = accessToken.getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (accessToken.isExpired() && refreshToken!=null && refreshToken.isValid()) {
            reissue(response, refreshToken);
        }
        filterChain.doFilter(request, response);
    }

    private AuthToken getAuthToken(HttpServletRequest request, String accessToken2) {
        AuthToken authToken = null;
        Optional<Cookie> accessTokenCookie = CookieUtil.getCookie(request, accessToken2);
        if (accessTokenCookie.isPresent()) {
            String accessTokenStr = accessTokenCookie.get().getValue();
            authToken = tokenProvider.convertAuthToken(accessTokenStr);
        }
        return authToken;
    }

    private void reissue(HttpServletResponse response, AuthToken refreshToken) {
        UUID refreshTokenPK = refreshToken.getTokenClaims().get(REFRESH_TOKEN_ID, UUID.class);
        String memberId = refreshToken.getTokenClaims().get(MEMBER_ID, String.class);
        String role = refreshToken.getTokenClaims().get(MEMBER_ROLE, String.class);
        RefreshToken savedRefreshToken = refreshTokenRepository         // token이 없을 때 예외 처리
                .findByIdAndMemberId(refreshTokenPK, memberId);

        if (refreshToken.getToken().equals(savedRefreshToken.getRefreshToken())) {
            AuthToken newAccessToken= tokenProvider.createAccessToken(memberId, role, new Date());
            UUID newRefreshTokenPK = UUID.randomUUID();
            AuthToken newRefreshToken = tokenProvider.createRefreshToken(newRefreshTokenPK, memberId, new Date());
            refreshTokenRepository.save(
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
