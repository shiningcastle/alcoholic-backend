package someone.alcoholic.service.oauth;

import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    MemberDto login(HttpServletResponse response, MemberLoginDto loginDto);
    void logout(HttpServletRequest request, HttpServletResponse response);
}
