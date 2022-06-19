package someone.alcoholic.service.mail;

import org.springframework.http.ResponseEntity;
import someone.alcoholic.dto.mail.AuthMailDto;
import someone.alcoholic.enums.MailType;

import javax.mail.MessagingException;

public interface MailService {
    ResponseEntity<?> sendAuthEmail(String email, MailType type) throws MessagingException;
    ResponseEntity<?> checkAuthEmail(AuthMailDto mailDto, MailType type);
}
