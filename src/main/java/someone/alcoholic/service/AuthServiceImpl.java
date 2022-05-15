package someone.alcoholic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.Member;
import someone.alcoholic.domain.RefreshToken;
import someone.alcoholic.dto.MemberLoginDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.MemberRepository;
import someone.alcoholic.repository.RefreshTokenRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public Member login(HttpServletResponse response, MemberLoginDto loginDto) {
        String memberId = loginDto.getEmail();
        String pw = loginDto.getPassword();
        Date now = new Date();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberId, pw)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String role = authentication.getAuthorities().stream().findAny()
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.VALUE_NOT_FOUND))
                .getAuthority();

        AuthToken accessToken = tokenProvider.createAccessToken(
                memberId, role, new Date(now.getTime() + tokenProvider.getAccessTokenMaxAge()));
        UUID refreshTokenId = UUID.randomUUID();
        AuthToken refreshToken = tokenProvider.createRefreshToken(
                refreshTokenId, memberId, new Date(now.getTime() + tokenProvider.getRefreshTokenMaxAge()));
        refreshTokenRepository.save(new RefreshToken(refreshTokenId, memberId, refreshToken.getToken()));

        setCookie(response, accessToken, refreshToken);

        return memberRepository.findByEmail(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.USER_NOT_EXIST));
    }

    private void setCookie(HttpServletResponse response, AuthToken accessToken, AuthToken refreshToken) {
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(),
                tokenProvider.getAccessTokenMaxAge() / 1000);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(),
                tokenProvider.getRefreshTokenMaxAge() / 1000);
    }
}
