package someone.alcoholic.service.member;

import io.jsonwebtoken.Claims;
import jdk.internal.net.http.common.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.dto.member.AccountDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.OAuthSignupDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.CookieExpiryTime;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.repository.member.TmpMemberRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.service.mail.MailService;
import someone.alcoholic.service.token.RefreshTokenService;
import someone.alcoholic.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TmpMemberRepository tmpMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenProvider authTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;

    @Transactional
    @Override
    public Member signup(MemberSignupDto signupDto) {
        checkSameIdOrNickname(signupDto);
        mailService.checkEmailCertified(MailType.SIGNUP, signupDto.getEmail());
        return memberRepository.save(Member.createLocalMember(
                signupDto.getId(), passwordEncoder.encode(signupDto.getPassword()),
                signupDto.getNickname(), signupDto.getEmail()));
    }

    @Transactional
    @Override
    public Member oAuthSignup(OAuthSignupDto signupDto, HttpServletRequest request, HttpServletResponse response) {
        String nickname = signupDto.getNickname();
        Cookie cookie = CookieUtil.getCookie(request, AuthToken.NICKNAME_TOKEN)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.TOKEN_NOT_EXIST));
        AuthToken nicknameToken = authTokenProvider.convertAuthToken(cookie.getValue());
        Claims tokenClaims = nicknameToken.getTokenClaims();
        if(tokenClaims == null) {
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.TOKEN_NOT_EXIST);
        }

        String memberId = tokenClaims.get(AuthToken.MEMBER_ID, String.class);
        Member member = tmpMemberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.TMP_USER_NOT_EXIST))
                .convertToMember(nickname);
        memberRepository.save(member);

        UUID refreshTokenPk = UUID.randomUUID();
        AuthToken accessToken = authTokenProvider.createAccessToken(memberId, member.getRole().toString());
        AuthToken refreshToken = authTokenProvider.createRefreshToken(refreshTokenPk, memberId);
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(), CookieExpiryTime.ACCESS_COOKIE_MAX_AGE);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(), CookieExpiryTime.REFRESH_COOKIE_MAX_AGE);
        refreshTokenService.save(refreshTokenPk, RefreshToken.builder().tokenValue(refreshToken.getToken()).memberId(memberId).accessToken(accessToken.getToken()).build());
        CookieUtil.deleteCookie(request, response, AuthToken.NICKNAME_TOKEN);
        return member;
    }

    @Override
    public Member findMemberById(String memberId) {
        log.info("회원 테이블 조회 시작 [조건] id = {}", memberId);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_NOT_EXIST));
    }

    // 아이디 찾기
    public String findMemberId(AccountDto accountDto) {
        String email = accountDto.getEmail();
        log.info("{} 아이디 찾기 요청 시작", email);
        mailService.checkEmailCertified(MailType.ID, email);
        Member member = getMemberByEmail(email);
        String id = member.getId();
        String returnId = id.substring(0, id.length() - 3) + "***";
        log.info("{} 아이디 찾기 요청 완료 - {}", email, returnId);
        return returnId;
    }

    // 비밀번호 변경
    public void changeMemberPassword(AccountDto accountDto) {
        String id = accountDto.getId();
        log.info("{} 비밀번호 변경 요청 시작", id);
        Member member = findMemberById(id);
        String password = member.getPassword();
        String inputPassword = accountDto.getPassword();
        checkCurrentPasswordEqual(id, password, inputPassword);
        changePassword(member, accountDto.getNewPassword());
        log.info("{} 비밀번호 변경 요청 완료", id);
    }

    // 비밀번호 초기화
    public void resetMemberPassword(AccountDto accountDto) {
        String id = accountDto.getId();
        String email = accountDto.getEmail();
        log.info("비밀번호 초기화 요청 시작 - id : {}, email : {}", id, email);
        mailService.checkEmailCertified(MailType.PASSWORD, email);
        Member member = getMemberByIdAndEmail(id, email);
        changePassword(member, accountDto.getNewPassword());
        log.info("비밀번호 초기화 완료 - id : {}, email : {}", id, email);
    }

    // 해당 아이디의 비밀번호를 새로운 비밀번호로 변경
    private void changePassword(Member member, String newPassword) {
        String id = member.getId();
        String password = member.getPassword();
        log.info("{} 새로운 비밀번호로 변경 시작", id);
        checkLastPasswordNotEqual(id, password, newPassword);
        String newEncryptedPassword = passwordEncoder.encode(newPassword);
        member.changePassword(newEncryptedPassword);
        memberRepository.save(member);
        log.info("{} 새로운 비밀번호로 변경 완료", id);
    }

    // 기존 비밀번호가 변경될 비밀번호와 일치하지 않는지 체크
    private void checkLastPasswordNotEqual(String id, String password, String newPassword) {
        if (passwordEncoder.matches(newPassword, password)) {
            log.info("{} 현재 비밀번호와 새 비밀번호 일치, 멤버 비밀번호 변경 불가 - password : {}", id, password);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_PASSWORD_SAME);
        }
        log.info("{} 현재 비밀번호와 새 비밀번호 불일치, 멤버 비밀번호 수정 가능", id);
    }

    // 입력 비밀번호가 해당 계정의 비밀번호가 맞는지 여부 체크
    private void checkCurrentPasswordEqual(String id, String password, String inputPassword) {
        log.info("{} 현재 비밀번호 일치 확인 시작 - password : {}", id, password);
        if (!passwordEncoder.matches(inputPassword, password)) {
            log.info("{} 현재 비밀번호 불일치, 멤버 객체 비밀번호 수정 불가 - password : {}", id, password);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_PASSWORD_INCORRECT);
        }
        log.info("{} 현재 비밀번호 일치 확인 완료 - password : {}", id, password);
    }

    // 회원 테이블 조회 (조건 : 이메일)
    private Member getMemberByEmail(String email) {
        log.info("회원 테이블 조회 시작 [조건] email = {}", email);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_NOT_EXIST));
    }

    // 회원 테이블 조회 (조건 : 아이디, 이메일)
    private Member getMemberByIdAndEmail(String id, String email) {
        log.info("회원 테이블 조회 시작 [조건] id = {}, email = {}", id, email);
        return memberRepository.findByIdAndEmail(id, email)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_NOT_EXIST));
    }

    private void checkSameIdOrNickname(MemberSignupDto signupDto) {
        memberRepository.findById(signupDto.getId())
                .ifPresent((mem) -> { throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_ALREADY_EXIST);});
        memberRepository.findByNickname(signupDto.getNickname())
                .ifPresent((mem) -> { throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.NICKNAME_ALREADY_EXIST);});
    }

}
