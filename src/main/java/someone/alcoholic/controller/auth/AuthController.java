package someone.alcoholic.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.OAuthSignupDto;
import someone.alcoholic.service.member.MemberService;
import someone.alcoholic.service.oauth.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ApiResult<MemberDto>> login(@RequestBody MemberLoginDto loginDto, HttpServletResponse response) {
        Member member = authService.login(response, loginDto);
        MemberDto memberDto = member.convertMemberDto();
        return ApiProvider.success(memberDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<ApiResult> logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        if(session != null) {
            session.invalidate();
        }
        authService.logout(request, response);
        return ApiProvider.success();
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResult> signup(@RequestBody MemberSignupDto signupDto) {
        memberService.signup(signupDto);
        return ApiProvider.success();
    }

    @PostMapping("/oauth/signup")
    public ResponseEntity<ApiResult<MemberDto>> oAuthSignup(@RequestBody OAuthSignupDto oAuthSignupDto,
                                            HttpServletRequest req, HttpServletResponse res) {
        Member member = memberService.oAuthSignup(oAuthSignupDto, req, res);
        MemberDto memberDto = member.convertMemberDto();
        return ApiProvider.success(memberDto);
    }
}
