package someone.alcoholic.service;

import someone.alcoholic.domain.Member;
import someone.alcoholic.domain.MemberLoginDto;
import someone.alcoholic.domain.MemberSignupDto;

public interface MemberService {
    Member signup(MemberSignupDto signupDto);
    Member login(MemberLoginDto loginDto);
}
