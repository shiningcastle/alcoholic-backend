package someone.alcoholic.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.service.oauth.AuthService;
import someone.alcoholic.service.member.MemberService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;


    @PostMapping("/login")
    public ApiResult<?> login(@RequestBody MemberLoginDto loginDto, HttpServletResponse response) {
        authService.login(response, loginDto);
        return ApiProvider.success();
    }

    @PostMapping("/signup")
    public ApiResult<?> signup(@RequestBody MemberSignupDto signupDto) {
        memberService.signup(signupDto);
        return ApiProvider.success();
    }

}
