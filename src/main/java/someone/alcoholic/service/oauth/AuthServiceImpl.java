package someone.alcoholic.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.CookieExpiryTime;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.service.token.TokenService;
import someone.alcoholic.util.CookieUtil;
import someone.alcoholic.util.S3Uploader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final S3Uploader s3Uploader;

    public MemberDto login(HttpServletResponse response, MemberLoginDto loginDto) {
        log.info("local 로그인 시작");
        String memberId = loginDto.getId();
        String pw = loginDto.getPassword();

        Authentication authentication = getAuthentication(memberId, pw);
        String role = getAuthority(authentication);

        AuthToken accessToken = tokenProvider.createAccessToken(memberId, role);
        UUID refreshTokenId = UUID.randomUUID();
        AuthToken refreshToken = tokenProvider.createRefreshToken(refreshTokenId, memberId);
        tokenService.save(refreshTokenId,
                new RefreshToken(refreshToken.getToken(), memberId, accessToken.getToken()));
        setCookie(response, accessToken, refreshToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_NOT_EXIST));
        log.info("local 로그인 성공, access. refresh token 생성");
        return new MemberDto(member.getNickname(), member.getEmail(), s3Uploader.s3PrefixUrl() + member.getImage(), member.getRole());
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
        log.info("로그아웃 시작");
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookie(request, AuthToken.REFRESH_TOKEN);
        if (refreshTokenCookie.isPresent()) {
            String refreshTokenStr = refreshTokenCookie.get().getValue();
            AuthToken refreshToken = tokenProvider.convertAuthToken(refreshTokenStr);
            UUID tokenId = UUID.fromString(refreshToken.getTokenClaims().get(AuthToken.REFRESH_TOKEN_ID, String.class));
            tokenService.delete(tokenId);
        }

        CookieUtil.deleteCookie(request, response, AuthToken.ACCESS_TOKEN);
        CookieUtil.deleteCookie(request, response, AuthToken.REFRESH_TOKEN);
        log.info("로그아웃 성공, accessToken, refreshToken cookie 제거");
    }
}
