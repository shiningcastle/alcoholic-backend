package someone.alcoholic.service.member;

import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberSignupDto;

public interface MemberService {
    Member signup(MemberSignupDto signupDto);
}
