package someone.alcoholic.service.mail;

import org.springframework.http.ResponseEntity;
import someone.alcoholic.dto.mail.MailDto;
import someone.alcoholic.enums.MailType;

import javax.mail.MessagingException;

public interface MailService {
    ResponseEntity<?> sendAuthEmail(String email, MailType type) throws MessagingException;
    ResponseEntity<?> checkAuthEmail(MailDto mailDto, MailType type);
}
