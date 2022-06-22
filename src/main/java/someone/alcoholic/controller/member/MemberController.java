package someone.alcoholic.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.member.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/account/{type}")
    public ResponseEntity<ApiResult<String>> findId() {
        String memberId = "";
        return ApiProvider.success(memberId, MessageEnum.MEMBER_ID_SUCCESS);
    }



}
