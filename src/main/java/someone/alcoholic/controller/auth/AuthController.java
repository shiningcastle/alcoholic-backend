package someone.alcoholic.controller.auth;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.NicknameDto;
import someone.alcoholic.service.member.MemberService;
import someone.alcoholic.service.oauth.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;

    @Operation(summary = "로컬 로그인", description = "아이디, 비밀번호를 이용하여 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResult<MemberDto>> login(@Valid @RequestBody @ApiParam(value = "로그인 정보", required = true) MemberLoginDto loginDto, HttpServletResponse response) {
        return ApiProvider.success(authService.login(response, loginDto));
    }

    @Operation(summary = "로그아웃", description = "유저 로그아웃")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<ApiResult> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ApiProvider.success();
    }

    @Operation(summary = "로컬 회원가입", description = "아이디, 비밀번호, 이메일을 입력하여 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResult> signup(@Valid @RequestBody @ApiParam(value = "회원가입 정보", required = true) MemberSignupDto signupDto) {
        memberService.signup(signupDto);
        return ApiProvider.success();
    }

    @Operation(summary = "OAuth 회원가입", description = "SNS 연동 로그인을 통한 회원가입 (닉네임 입력) /oauth2/authorization/google  /oauth2/authorization/kakao")
    @PostMapping("/oauth/signup")
    public ResponseEntity<ApiResult<MemberDto>> oAuthSignup(@Valid @RequestBody @ApiParam(value = "추가 닉네임 정보", required = true) NicknameDto nicknameDto,
                                            HttpServletRequest req, HttpServletResponse res) {
        return ApiProvider.success(memberService.oAuthSignup(nicknameDto, req, res));
    }
}
