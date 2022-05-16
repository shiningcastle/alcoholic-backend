package someone.alcoholic.service.oauth;

import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.auth.MemberLoginDto;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    Member login(HttpServletResponse response, MemberLoginDto loginDto);
}
