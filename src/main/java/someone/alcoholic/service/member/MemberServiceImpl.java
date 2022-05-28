package someone.alcoholic.service.member;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.member.TmpMember;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.OAuthSignupDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.ExpiryTime;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.repository.member.TmpMemberRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TmpMemberRepository tmpMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenProvider authTokenProvider;

    @Override
    public Member signup(MemberSignupDto signupDto) {
        checkSameIdOrNickname(signupDto);
        return memberRepository.save(Member.createLocalMember(
                signupDto.getId(), passwordEncoder.encode(signupDto.getPassword()),
                signupDto.getNickname(), signupDto.getEmail()));
    }

    private void checkSameIdOrNickname(MemberSignupDto signupDto) {
        memberRepository.findById(signupDto.getId())
                .ifPresent((mem) -> { throw new CustomRuntimeException(ExceptionEnum.USER_ALREADY_EXIST);});
        memberRepository.findByNickname(signupDto.getNickname())
                .ifPresent((mem) -> { throw new CustomRuntimeException(ExceptionEnum.NICKNAME_ALREADY_EXIST);});
    }

    @Override
    public Member oAuthSignup(OAuthSignupDto signupDto, HttpServletRequest request, HttpServletResponse response) {
        String nickname = signupDto.getNickname();

        Cookie cookie = CookieUtil.getCookie(request, AuthToken.NICKNAME_TOKEN)
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.TOKEN_NOT_EXIST));
        AuthToken nicknameToken = authTokenProvider.convertAuthToken(cookie.getValue());
        Claims tokenClaims = nicknameToken.getTokenClaims();
        if(tokenClaims == null) {
            throw new CustomRuntimeException(ExceptionEnum.TOKEN_NOT_EXIST);
        }

        String memberId = tokenClaims.get(AuthToken.MEMBER_ID, String.class);
        Member member = tmpMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.TMP_USER_NOT_EXIST))
                .convertToMember(nickname);
        memberRepository.save(member);

        UUID refreshTokenPk = UUID.randomUUID();
        AuthToken accessToken = authTokenProvider.createAccessToken(memberId, member.getRole().toString());
        AuthToken refreshToken = authTokenProvider.createRefreshToken(refreshTokenPk, memberId);
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(), ExpiryTime.ACCESS_COOKIE_EXPIRY_TIME);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(), ExpiryTime.REFRESH_COOKIE_EXPIRY_TIME);
        //refreshTokenRepository.save(new RefreshToken(refreshTokenPk.toString(), memberId, refreshToken.getToken()));
        CookieUtil.deleteCookie(request, response, AuthToken.NICKNAME_TOKEN);
        return member;
    }
}
