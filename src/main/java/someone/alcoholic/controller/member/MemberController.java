package someone.alcoholic.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import someone.alcoholic.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


}
