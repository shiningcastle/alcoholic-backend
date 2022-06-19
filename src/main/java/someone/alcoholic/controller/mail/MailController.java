package someone.alcoholic.controller.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.dto.mail.MailDto;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.service.mail.MailService;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @GetMapping("/{type}")
    public ResponseEntity<?> sendAuthEmail(@RequestParam @Email String email, @PathVariable String type) throws MessagingException {
        return mailService.sendAuthEmail(email, MailType.valueOf(type.toUpperCase()));
    }

    @PostMapping("/{type}")
    public ResponseEntity<?> checkAuthEmail(@RequestBody MailDto mailDto, @PathVariable String type) {
        return mailService.checkAuthEmail(mailDto, MailType.valueOf(type.toUpperCase()));
    }
}
