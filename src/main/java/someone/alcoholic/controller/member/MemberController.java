package someone.alcoholic.controller.member;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.member.AccountDto;
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.member.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @Secured("ROLE_USER")
    @GetMapping("/info")
    public ResponseEntity<ApiResult<MemberDto>> getMemberDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberService.findMemberById(authentication.getName());
        return ApiProvider.success(member.convertMemberDto());
    }

    @Operation(summary = "유저 아이디 찾기", description = "이메일 인증을 통한 아이디 찾기")
    @GetMapping("/forget/id")
    public ResponseEntity<ApiResult<String>> findId(@RequestParam @Valid @ApiParam(value = "이메일", required = true) String email) {
        return ApiProvider.success(memberService.findMemberId(email), MessageEnum.MEMBER_ID_SUCCESS);
    }


    @Operation(summary = "유저 비밀번호 초기화", description = "비밀번호 분실시 유저 계정의 비밀번호를 이메일 인증을 통해 재설정")
    @PostMapping("/forget/password")
    public ResponseEntity<ApiResult> resetPassWord(@RequestBody @Valid @ApiParam(value = "비밀번호 초기화 정보", required = true) AccountDto accountDto) {
        memberService.resetMemberPassword(accountDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }
    
    
    @Operation(summary = "유저 비밀번호 변경", description = "유저 계정의 기존 비밀번호를 새 비밀번호로 변경")
    @PostMapping("/change/password")
    public ResponseEntity<ApiResult> changePassWord(@RequestBody @Valid @ApiParam(value = "비밀번호 변경 정보", required = true) AccountDto accountDto) {
        memberService.changeMemberPassword(accountDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }



}
