package someone.alcoholic.service;

import someone.alcoholic.domain.Member;
import someone.alcoholic.dto.MemberLoginDto;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    Member login(HttpServletResponse response, MemberLoginDto loginDto);
}
