package someone.alcoholic.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.member.AccountDto;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.member.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/forget/id")
    public ResponseEntity<ApiResult<String>> findId(@RequestBody AccountDto accountDto) {
        return ApiProvider.success(memberService.findMemberId(accountDto), MessageEnum.MEMBER_ID_SUCCESS);
    }

    @PostMapping("/forget/password")
    public ResponseEntity<ApiResult> resetPassWord(@RequestBody AccountDto accountDto) {
        memberService.resetMemberPassword(accountDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }

    @PostMapping("/change/password")
    public ResponseEntity<ApiResult> changePassWord(@RequestBody AccountDto accountDto) {
        memberService.changeMemberPassword(accountDto);
        return ApiProvider.success(MessageEnum.MEMBER_PASSWORD_SUCCESS);
    }



}
