package someone.alcoholic.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.member.TmpMember;
import someone.alcoholic.domain.oauth.OAuth2Attribute;
import someone.alcoholic.enums.CookieExpiryTime;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.repository.member.TmpMemberRepository;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessfulHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final TmpMemberRepository tmpMemberRepository;
    private final AuthTokenProvider authTokenProvider;
    private final OAuth2Attribute oAuth2Attribute;

    @Value("${front.url}")
    private String frontUrl;

    @Value("${oauth.new-suffix}")
    private String newMemberSuffix;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2Member principal = (OAuth2Member) authentication.getPrincipal();
        String memberId = authentication.getName();
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member != null) {
            issueAccessAndRefreshToken(response, memberId, member);
            response.sendRedirect(frontUrl);
        } else {
            saveTmpMember(request, response, memberId, principal);
            response.sendRedirect(frontUrl + newMemberSuffix);
        }
    }

    private void saveTmpMember(HttpServletRequest request, HttpServletResponse response, String memberId, OAuth2Member principal) {
        log.info("최초 oAuth2 로그인, nicknameToken 발행");
        Optional<Cookie> cookie = CookieUtil.getCookie(request, AuthToken.NICKNAME_TOKEN);
        if (!cookie.isPresent()) {  // nicknameToken이 없다면 발급, 있다면 그냥 지나감 (있다는 것은 닉네임 과정을 갔지만 닉네임 발급을 안 한 것)
            AuthToken nicknameToken = authTokenProvider.createNicknameToken(memberId);
            CookieUtil.addCookie(response, AuthToken.NICKNAME_TOKEN, nicknameToken.getToken(), CookieExpiryTime.NICKNAME_COOKIE_EXPIRY_TIME);
            Map<String, Object> attributes = principal.getAttributes();
            TmpMember tmpMember = oAuth2Attribute.getAttribute(principal.getProvider(), attributes).toTmpMember();
            if (!tmpMemberRepository.findById(tmpMember.getId()).isPresent()) {
                tmpMemberRepository.save(tmpMember);
                log.info("최초 oAuth2 로그인, tmpMember 저장 {}", tmpMember);
            } else {
                log.info("최초 oAuth2 로그인, 닉네임 토큰 새로 발급함 + tmpMember는 이미 존재");
            }
        } else {
            log.info("최초 oAuth2 로그인, 닉네임 토큰 이미 있음");
        }
    }

    private void issueAccessAndRefreshToken(HttpServletResponse response, String memberId, Member member) {
        AuthToken accessToken = authTokenProvider.createAccessToken(memberId, member.getRole().getRoleName());
        AuthToken refreshToken = authTokenProvider.createRefreshToken(UUID.randomUUID(), memberId);
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(), CookieExpiryTime.ACCESS_COOKIE_MAX_AGE);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(), CookieExpiryTime.REFRESH_COOKIE_MAX_AGE);
    }
}
