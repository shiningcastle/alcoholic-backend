package someone.alcoholic.service;

import someone.alcoholic.domain.Member;
import someone.alcoholic.dto.MemberLoginDto;
import someone.alcoholic.dto.MemberSignupDto;

public interface MemberService {
    Member signup(MemberSignupDto signupDto);
    Member login(MemberLoginDto loginDto);
}
