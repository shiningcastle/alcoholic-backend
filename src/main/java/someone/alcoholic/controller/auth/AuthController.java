package someone.alcoholic.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.mail.AuthMailDto;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.OAuthSignupDto;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.service.mail.MailService;
import someone.alcoholic.service.member.MemberService;
import someone.alcoholic.service.oauth.AuthService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final MailService mailService;

    @PostMapping("/login")
    public ApiResult<MemberDto> login(@RequestBody MemberLoginDto loginDto, HttpServletResponse response) {
        Member member = authService.login(response, loginDto);
        MemberDto memberDto = member.convertMemberDto();
        return ApiProvider.success(memberDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ApiResult<?> logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        if(session != null) {
            session.invalidate();
        }
        authService.logout(request, response);
        return ApiProvider.success();
    }

    @GetMapping("/email-send/{type}")
    public ResponseEntity<?> sendAuthEmail(@RequestParam @Email String email, @PathVariable String type) throws MessagingException {
        return mailService.sendAuthEmail(email, MailType.valueOf(type.toUpperCase()));
    }

    @PostMapping("/email-check/{type}")
    public ResponseEntity<?> checkAuthEmail(@RequestBody AuthMailDto authMailDto, @PathVariable String type) {
        return mailService.checkAuthEmail(authMailDto, MailType.valueOf(type.toUpperCase()));
    }

    @PostMapping("/signup")
    public ApiResult<?> signup(@RequestBody MemberSignupDto signupDto) {
        memberService.signup(signupDto);
        return ApiProvider.success();
    }

    @PostMapping("/oauth/signup")
    public ApiResult<MemberDto> oAuthSignup(@RequestBody OAuthSignupDto oAuthSignupDto,
                                            HttpServletRequest req, HttpServletResponse res) {
        Member member = memberService.oAuthSignup(oAuthSignupDto, req, res);
        MemberDto memberDto = member.convertMemberDto();
        return ApiProvider.success(memberDto);
    }
}
