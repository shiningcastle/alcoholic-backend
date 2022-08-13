package someone.alcoholic.service.member;

import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.member.AccountDto;
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.NicknameDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public interface MemberService {
    Member signup(MemberSignupDto signupDto);
    MemberDto oAuthSignup(NicknameDto signupDto, HttpServletRequest request, HttpServletResponse response);
    Member findMemberById(String memberId);
    String findMemberId(String email);
    void resetMemberPassword(AccountDto accountDto);
    void changeMemberPassword(Principal principal, AccountDto accountDto);
    void changeMemberNickname(Principal principal, String memberId, NicknameDto nicknameDto);
    void changeMemberImage(Principal principal, String memberId, MultipartFile multipartFile);
    void deleteMemberImage(Principal principal, String memberId);
}
