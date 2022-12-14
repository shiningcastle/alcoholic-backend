package someone.alcoholic.controller.mail;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.service.mail.MailService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @Operation(summary = "인증 이메일 전송", description = "유저가 입력한 이메일 주소로 인증 링크가 담긴 이메일을 전송")
    @GetMapping("/send/{type}")
    //example = "signup, id, password"
    public ResponseEntity<ApiResult> sendAuthEmail(@PathVariable @NotEmpty @ApiParam(value = "인증유형 타입", required = true, allowableValues = "signup, id, password") String type,
                                                   @RequestParam @Email(message = "이메일은 필수 입력 값입니다.") @ApiParam(value = "이메일", required = true, example = "test1234@gmail.com") String email) throws MessagingException {
        return mailService.sendEmail(MailType.valueOf(type.toUpperCase()), email);
    }

    @Operation(summary = "이메일 인증링크 확인", description = "이메일의 인증 URL을 클릭했을 때 인증번호를 받아서 인증 여부를 등록 (Frontend 사용 X)")
    @GetMapping("/auth/{type}")
    public void checkAuthEmail(@PathVariable @NotEmpty @ApiParam(value = "인증유형 타입", required = true, allowableValues = "signup, id, password") String type,
                               @RequestParam @Email(message = "이메일은 필수 입력 값입니다.") @ApiParam(value = "이메일", required = true, example = "test1234@gmail.com") String email,
                                            @Length(min = 100000, max = 999999, message = "인증번호의 자리수가 일치하지 않습니다.") @RequestParam @Positive @ApiParam(value = "난수", required = true, example = "123456") int number,
                                            HttpServletResponse response) {
        mailService.authEmail(MailType.valueOf(type.toUpperCase()), email, number, response);
    }

    @Operation(summary = "이메일 인증기록 조회", description = "해당 이메일로 특정 부분의 인증이 완료되었는지 여부를 확인")
    @GetMapping("/check/{type}")
    public ResponseEntity<ApiResult> checkAuthEmail(@PathVariable @NotEmpty @ApiParam(value = "인증유형 타입", required = true, allowableValues = "signup, id, password") String type, @RequestParam @Email(message = "이메일은 필수 입력 값입니다.") String email) {
        return mailService.checkEmailCertified(MailType.valueOf(type.toUpperCase()), email);
    }
}
