package someone.alcoholic.service.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.CookieExpiryTime;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.service.token.RefreshTokenService;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    public Member login(HttpServletResponse response, MemberLoginDto loginDto) {
        String memberId = loginDto.getId();
        String pw = loginDto.getPassword();

        Authentication authentication = getAuthentication(memberId, pw);
        String role = getAuthority(authentication);

        AuthToken accessToken = tokenProvider.createAccessToken(memberId, role);
        UUID refreshTokenId = UUID.randomUUID();
        AuthToken refreshToken = tokenProvider.createRefreshToken(refreshTokenId, memberId);
        refreshTokenService.save(refreshTokenId,
                new RefreshToken(refreshToken.getToken(), memberId, accessToken.getToken()));
        setCookie(response, accessToken, refreshToken);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_NOT_EXIST));
    }

    private Authentication getAuthentication(String memberId, String pw) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberId, pw));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private String getAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream().findAny()
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.VALUE_NOT_FOUND))
                .getAuthority();
    }

    private void setCookie(HttpServletResponse response, AuthToken accessToken, AuthToken refreshToken) {
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(),
                CookieExpiryTime.ACCESS_COOKIE_MAX_AGE);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(),
                CookieExpiryTime.REFRESH_COOKIE_MAX_AGE);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookie(request, AuthToken.REFRESH_TOKEN);
        if (refreshTokenCookie.isPresent()) {
            String refreshTokenStr = refreshTokenCookie.get().getValue();
            AuthToken refreshToken = tokenProvider.convertAuthToken(refreshTokenStr);
            UUID tokenId = UUID.fromString(refreshToken.getTokenClaims().get(AuthToken.REFRESH_TOKEN_ID, String.class));
            refreshTokenService.delete(tokenId);
        }

        CookieUtil.deleteCookie(request, response, AuthToken.ACCESS_TOKEN);
        CookieUtil.deleteCookie(request, response, AuthToken.REFRESH_TOKEN);
    }
}
