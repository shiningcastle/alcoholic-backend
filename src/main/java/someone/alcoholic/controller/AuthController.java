package someone.alcoholic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import someone.alcoholic.domain.MemberLoginDto;
import someone.alcoholic.domain.MemberSignupDto;
import someone.alcoholic.domain.RefreshToken;
import someone.alcoholic.repository.RefreshTokenRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.service.MemberService;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberService memberService;


    @PostMapping("/login")
    public void login(@RequestBody MemberLoginDto loginDto, HttpServletResponse response) {
        String memberId = loginDto.getEmail();
        String pw = loginDto.getPassword();
        Date now = new Date();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberId, pw)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String role = authentication.getAuthorities().stream().findAny()
                .orElseThrow(() -> new RuntimeException("authority가 비었다.")).getAuthority();
        AuthToken accessToken = tokenProvider.createAccessToken(
                memberId, role, new Date(now.getTime() + tokenProvider.getAccessTokenMaxAge()));
        UUID refreshTokenId = UUID.randomUUID();
        AuthToken refreshToken = tokenProvider.createRefreshToken(
                refreshTokenId, memberId, new Date(now.getTime() + tokenProvider.getRefreshTokenMaxAge()));
        refreshTokenRepository.save(new RefreshToken(refreshTokenId, memberId, refreshToken.getToken()));

        setCookie(response, accessToken, refreshToken);
    }

    private void setCookie(HttpServletResponse response, AuthToken accessToken, AuthToken refreshToken) {
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(),
                tokenProvider.getAccessTokenMaxAge() / 1000);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(),
                tokenProvider.getRefreshTokenMaxAge() / 1000);
    }

    @PostMapping("/signup")
    public void signup(@RequestBody MemberSignupDto signupDto) {
        memberService.signup(signupDto);
    }

}
