package someone.alcoholic.service.member;

import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.OAuthSignupDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MemberService {
    Member signup(MemberSignupDto signupDto);
    Member oAuthSignup(OAuthSignupDto signupDto, HttpServletRequest request, HttpServletResponse response);
    Member findMemberById(String memberId);
    String findMemberId(String email);
}
