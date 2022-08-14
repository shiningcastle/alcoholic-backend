package someone.alcoholic.service.member;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.dto.member.*;
import someone.alcoholic.enums.*;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.NicknameRepository;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.repository.member.TmpMemberRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.service.mail.MailService;
import someone.alcoholic.service.token.TokenService;
import someone.alcoholic.util.FileUtil;
import someone.alcoholic.util.CookieUtil;
import someone.alcoholic.util.S3Uploader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    @Value("${image.member.directory}")
    private String imageDirectory;
    @Value("${image.member.default}")
    private String profileDefaultImage;
    private final MemberRepository memberRepository;
    private final TmpMemberRepository tmpMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenProvider authTokenProvider;
    private final TokenService tokenService;
    private final MailService mailService;
    private final NicknameRepository nicknameRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    @Override
    public Member signup(MemberSignupDto signupDto) {
        log.info("회원가입 시작");
        checkDuplicatedId(signupDto);
        mailService.checkEmailCertified(MailType.SIGNUP, signupDto.getEmail());
        String randomAdj = nicknameRepository.findRandomAdj();
        String randomNoun = nicknameRepository.findRandomNoun();
        int randomNum = ThreadLocalRandom.current().nextInt(99999);
        String nickname = randomNum + "번 " + randomAdj + "한 " + randomNoun;
        return memberRepository.save(Member.createLocalMember(
                signupDto.getId(), passwordEncoder.encode(signupDto.getPassword()),
                nickname, FileUtil.buildDefaultFilePath(imageDirectory, profileDefaultImage), signupDto.getEmail()));
    }

    public MemberDto findMember(String id, Principal principal) {
        log.info("{} 유저 본인 정보 조회", id);
        checkAuthorizedUser(principal, id);
        Member member = findMemberById(id);
        log.info("{} 유저 본인 정보 조회 성공", id);
        return MemberDto.convertMemberDto(member, s3Uploader.s3PrefixUrl());
    }

    @Transactional
    @Override
    public MemberDto oAuthSignup(NicknameDto signupDto, HttpServletRequest request, HttpServletResponse response) {
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
                .convertToMember(nickname, FileUtil.buildDefaultFilePath(imageDirectory, profileDefaultImage));
        memberRepository.save(member);

        UUID refreshTokenPk = UUID.randomUUID();
        AuthToken accessToken = authTokenProvider.createAccessToken(memberId, member.getRole().toString());
        AuthToken refreshToken = authTokenProvider.createRefreshToken(refreshTokenPk, memberId);
        CookieUtil.addCookie(response, AuthToken.ACCESS_TOKEN, accessToken.getToken(), CookieExpiryTime.ACCESS_COOKIE_MAX_AGE);
        CookieUtil.addCookie(response, AuthToken.REFRESH_TOKEN, refreshToken.getToken(), CookieExpiryTime.REFRESH_COOKIE_MAX_AGE);
        tokenService.save(refreshTokenPk, RefreshToken.builder().tokenValue(refreshToken.getToken()).memberId(memberId).accessToken(accessToken.getToken()).build());
        CookieUtil.deleteCookie(request, response, AuthToken.NICKNAME_TOKEN);
        return MemberDto.convertMemberDto(member, s3Uploader.s3PrefixUrl());
    }

    @Override
    public Member findMemberById(String memberId) {
        log.info("회원 테이블 조회 시작 [조건] id = {}", memberId);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_NOT_EXIST));
    }

    public void checkDuplicateNickname(String nickname) {
        log.info("유저 닉네임 중복체크 시작 - nickname : {}", nickname);
        Optional<Member> memberOpt = memberRepository.findByNickname(nickname);
        if (memberOpt.isPresent()) {
            log.info("유저 닉네임 중복체크 탈락 - nickname : {} 존재", nickname);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.NICKNAME_ALREADY_EXIST);
        }
    }

    // 아이디 찾기
    public String findMemberId(String email) {
        log.info("{} 아이디 찾기 요청 시작", email);
        mailService.checkEmailCertified(MailType.ID, email);
        Member member = getMemberByEmail(email);
        String id = member.getId();
        String returnId = id.substring(0, id.length() - 3) + "***";
        log.info("{} 아이디 찾기 요청 완료 - {}", email, returnId);
        return returnId;
    }

    // 비밀번호 변경
    @Transactional(rollbackFor = {Exception.class})
    public void changeMemberPassword(Principal principal, String id, newPasswordChangeDto passwordChangeDto) {
        log.info("{} 멤버 - 비밀번호 변경 요청 시작", id);
        checkAuthorizedUser(principal, id);
        Member member = findMemberById(id);
        String password = member.getPassword();
        String inputPassword = passwordChangeDto.getPassword();
        checkCurrentPasswordEqual(id, password, inputPassword);
        changePassword(member, passwordChangeDto.getNewPassword());
        log.info("{} 비밀번호 변경 요청 완료", id);
    }

    // 비밀번호 초기화
    @Transactional(rollbackFor = {Exception.class})
    public void resetMemberPassword(String id, newPasswordResetDto passwordResetDto) {
        log.info("비밀번호 초기화 요청 시작 - id : {}", id);
        String email = passwordResetDto.getEmail();
        log.info("비밀번호 초기화 요청 시작 - email : {}", email);
        mailService.checkEmailCertified(MailType.PASSWORD, email);
        Member member = getMemberByIdAndEmail(id, email);
        changePassword(member, passwordResetDto.getNewPassword());
        log.info("비밀번호 초기화 완료 - id : {}, email : {}", id, email);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void changeMemberNickname(Principal principal, String id, NicknameDto nicknameDto) {
        log.info("{} 유저 닉네임 변경 요청 시작", id);
        checkAuthorizedUser(principal, id);
        String newNickname = nicknameDto.getNickname();
        Member member = findMemberById(id);
        String currentNickName = member.getNickname();
        log.info("{} 유저 닉네임 - current : {} -> new : {}", id, currentNickName, newNickname);
        checkDuplicateNickname(newNickname);
        member.setNickname(newNickname);
        Member savedMember = memberRepository.save(member);
        log.info("{} 유저 닉네임 변경 요청 완료 - nickName : {} -> {}", id, currentNickName, savedMember.getNickname());
    }

    @Transactional(rollbackFor = {Exception.class})
    public void changeMemberImage(Principal principal, String id, MultipartFile multipartFile) {
        log.info("{} 유저 이미지 변경 요청 시작 - file : {}", id, multipartFile.getName());
        checkAuthorizedUser(principal, id);
        Member member = findMemberById(id);
        String memberImageUrl = member.getImage();
        log.info("{} 유저 currentImage : {}", id, memberImageUrl);
        FileUtil.validateFile(multipartFile); // 파일 존재 체크
        s3Uploader.deleteFile(memberImageUrl); // 기존 파일 삭제
        String filePath = FileUtil.buildFilePath(multipartFile, imageDirectory, member.getSeq()); //저장 경로
        s3Uploader.uploadFile(multipartFile, filePath);
        member.setImage(filePath);
        memberRepository.save(member);
        log.info("{} 유저 이미지 변경 완료 - path : {}", id, filePath);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteMemberImage(Principal principal, String id) {
        log.info("{} 유저 이미지 삭제 요청 시작", id);
        checkAuthorizedUser(principal, id);
        Member member = findMemberById(id);
        String memberImageUrl = member.getImage();
        log.info("{} 유저 currentImage : {}", id, memberImageUrl);
        if(!s3Uploader.deleteFile(memberImageUrl)) {
            log.info("{} 유저 기본 이미지 상태에서 삭제 요청");
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_REMOVE_NOT_ALLOWED);
        }
        // 기본 이미지로 변경
        String defaultImagePath = FileUtil.buildDefaultFilePath(imageDirectory, profileDefaultImage);
        member.setImage(defaultImagePath);
        memberRepository.save(member);
        log.info("{} 유저 이미지 삭제 완료 - 기본 이미지로 변경 : {}", id, defaultImagePath);
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

    // 로그인 계정이 정보변경 계정과 일치하는지 체크
    public void checkAuthorizedUser(Principal principal, String id) {
        String loginId = getLoginUserId(principal);
        if (!id.equals(loginId)) {
            log.info("로그인 계정과 정보변경 요청 계정 불일치 - login : {}, request : {}", loginId, id);
            throw new CustomRuntimeException(HttpStatus.FORBIDDEN, ExceptionEnum.FORBIDDEN);
        }
        log.info("로그인 계정과 정보변경 요청 계정 일치 - login : {}, request : {}", loginId, id);
    }

    public String getLoginUserId(Principal principal) {
        if (principal == null) {
            log.info("로그인 상태 확인 불가");
            throw new CustomRuntimeException(HttpStatus.UNAUTHORIZED, ExceptionEnum.UNAUTHORIZED);
        }
        String loginId = principal.getName();
        log.info("로그인 상태 확인 - {} 유저", loginId);
        return loginId;
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

    private void checkDuplicatedId(MemberSignupDto signupDto) {
        memberRepository.findById(signupDto.getId())
                .ifPresent((mem) -> { throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_ALREADY_EXIST);});
        log.info("해당하는 id가 없다. id = {}", signupDto.getId());
    }

}
