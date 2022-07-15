package someone.alcoholic.service.member;

import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.member.AccountDto;
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.OAuthSignupDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MemberService {
    Member signup(MemberSignupDto signupDto);
    MemberDto oAuthSignup(OAuthSignupDto signupDto, HttpServletRequest request, HttpServletResponse response);
    Member findMemberById(String memberId);
    String findMemberId(AccountDto accountDto);
    void resetMemberPassword(AccountDto accountDto);
    void changeMemberPassword(AccountDto accountDto);
}
