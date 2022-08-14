package someone.alcoholic.service.member;

import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.member.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public interface MemberService {
    Member signup(MemberSignupDto signupDto);
    MemberDto oAuthSignup(NicknameDto signupDto, HttpServletRequest request, HttpServletResponse response);
    Member findMemberById(String memberId);
    MemberDto findMember(String id, Principal principal);
    String findMemberId(String email);
    void resetMemberPassword(String id, newPasswordResetDto passwordResetDto);
    void changeMemberPassword(Principal principal, String id, newPasswordChangeDto passwordChangeDto);
    void changeMemberNickname(Principal principal, String id, NicknameDto nicknameDto);
    void changeMemberImage(Principal principal, String id, MultipartFile multipartFile);
    void deleteMemberImage(Principal principal, String id);
    String getLoginUserId(Principal principal);
    void checkAuthorizedUser(Principal principal, String id);
}
