package someone.alcoholic.controller.mail;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.dto.mail.MailDto;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.service.mail.MailService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @ResponseBody
    @GetMapping("/send/{type}")
    public ResponseEntity<?> sendAuthEmail(@PathVariable String type, @RequestBody MailDto mailDto) throws MessagingException {
        return mailService.sendAuthEmail(MailType.valueOf(type.toUpperCase()), mailDto);
    }

    @GetMapping("/check/{type}")
    public void checkAuthEmail(@PathVariable String type, @RequestParam @Email(message = "이메일은 필수 입력 값입니다.") String email,
                                            @Length(min = 100000, max = 999999, message = "인증번호의 자리수가 일치하지 않습니다.") @RequestParam int number,
                                            HttpServletResponse response) {
        mailService.checkAuthEmail(MailType.valueOf(type.toUpperCase()), email, number, response);
    }
}
