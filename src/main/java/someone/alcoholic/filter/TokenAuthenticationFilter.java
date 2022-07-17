package someone.alcoholic.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.service.token.TokenService;
import someone.alcoholic.util.IpUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthToken accessToken = tokenService.getAuthToken(request, AuthToken.ACCESS_TOKEN);
        String accessTokenValue = (accessToken != null) ? accessToken.getToken() : null;
        String accessIp = IpUtil.getClientIP(request);
        // accessToken 만료시 관련 쿠키는 시간 설정에 의해 삭제되므로 accessToken == null
        if (accessToken != null && accessToken.isValid()) {
            log.info("{} - accessToken is valid : {}", accessIp, accessTokenValue);
            Authentication authentication = accessToken.getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication : {}", authentication);
        } else if (accessToken != null && !accessToken.isValid()) {
            AuthToken refreshToken = tokenService.getAuthToken(request, AuthToken.REFRESH_TOKEN);
            if (accessToken.isExpired() && refreshToken != null) {
                UUID tokenId = UUID.fromString(refreshToken.getTokenClaims().get(AuthToken.REFRESH_TOKEN_ID, String.class));
                RefreshToken savedRefreshToken = tokenService.findById(tokenId);
                String refreshTokenValue = refreshToken.getToken();
                String savedRefreshTokenValue = (savedRefreshToken != null) ? savedRefreshToken.getTokenValue() : null;
                if (refreshTokenValue != null && refreshTokenValue.equals(savedRefreshTokenValue)) {
                    String savedAccessTokenValue = savedRefreshToken.getAccessToken();
                    if (accessTokenValue.equals(savedAccessTokenValue)) {
                        log.info("{} - accessToken is expired : {}", accessIp, accessTokenValue);
                        tokenService.reissue(response, tokenId, savedRefreshToken, refreshToken);
                    } else {
                        tokenService.delete(tokenId);
                        log.info("{} - expires all refreshTokens : {}", accessIp, refreshTokenValue);
                    }
                } else {
                    log.info("{} - refreshToken is different {} - {}", accessIp, refreshTokenValue, savedRefreshTokenValue);
                }
            } else if (!accessToken.isExpired() && refreshToken != null) {
                log.info("{} - accessToken is inValid : {}", accessIp, accessTokenValue);
            } else {
                log.info("{} - refreshToken is null : {}", accessIp, refreshToken);
            }
        } else {
            log.info("{} - accessToken is null", accessIp);
        }
        filterChain.doFilter(request, response);
    }
}
