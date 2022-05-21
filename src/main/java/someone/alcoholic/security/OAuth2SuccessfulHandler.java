package someone.alcoholic.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.oauth.OAuth2Attribute;
import someone.alcoholic.enums.Age;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessfulHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final AuthTokenProvider authTokenProvider;
    private final OAuth2Attribute oAuth2Attribute;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2Member principal = (OAuth2Member) authentication.getPrincipal();
        String memberId = authentication.getName();
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member != null) {
            issueAccessAndRefreshToken(response, memberId, member);
            response.sendRedirect("/");
        } else {
            issueNicknameToken(response, principal);
            response.sendRedirect("/nickname");
        }
    }

    private void issueNicknameToken(HttpServletResponse response, OAuth2Member principal) {
        log.info("최초 oAuth2 로그인, nicknameToken 발행");
        Map<String, Object> attributes = principal.getAttributes();
        Member tmpMember = oAuth2Attribute.getAttribute(principal.getProvider(), attributes).toEntity();
        AuthToken nicknameToken = authTokenProvider.createNicknameToken(tmpMember);
        CookieUtil.addCookie(response, AuthToken.NICKNAME_TOKEN, nicknameToken.getToken(), Age.NICKNAME_COOKIE_MAX_AGE);
    }

    private void issueAccessAndRefreshToken(HttpServletResponse response, String memberId, Member member) {
        AuthToken accessToken = authTokenProvider.createAccessToken(memberId, member.getRole().getRoleName());
        AuthToken refreshToken = authTokenProvider.createRefreshToken(UUID.randomUUID(), memberId);
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(), Age.ACCESS_COOKIE_MAX_AGE);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(), Age.REFRESH_COOKIE_MAX_AGE);
    }
}
