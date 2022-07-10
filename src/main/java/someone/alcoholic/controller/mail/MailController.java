package someone.alcoholic.controller.mail;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    // 인증 이메일 생성, 전송 API
    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제한다.")
    @GetMapping("/send/{type}")
    public ResponseEntity<ApiResult> sendAuthEmail(@PathVariable String type, @RequestParam @Email(message = "이메일은 필수 입력 값입니다.") String email) throws MessagingException {
        return mailService.sendEmail(MailType.valueOf(type.toUpperCase()), email);
    }

    // 인증 이메일 링크 확인 API
    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제한다.")
    @GetMapping("/auth/{type}")
    public void checkAuthEmail(@PathVariable String type, @RequestParam @Email(message = "이메일은 필수 입력 값입니다.") String email,
                                            @Length(min = 100000, max = 999999, message = "인증번호의 자리수가 일치하지 않습니다.") @RequestParam int number,
                                            HttpServletResponse response) {
        mailService.authEmail(MailType.valueOf(type.toUpperCase()), email, number, response);
    }

    // 이메일 인증 기록 조회
    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제한다.")
    @GetMapping("/check/{type}")
    public ResponseEntity<ApiResult> checkAuthEmail(@PathVariable String type, @RequestParam @Email(message = "이메일은 필수 입력 값입니다.") String email) {
        return mailService.checkEmailCertified(MailType.valueOf(type.toUpperCase()), email);
    }
}
