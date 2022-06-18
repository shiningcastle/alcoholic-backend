package someone.alcoholic.service.mail;

import org.springframework.http.ResponseEntity;
import someone.alcoholic.dto.MailDto;

import javax.mail.MessagingException;

public interface MailService {
    ResponseEntity<?> sendAuthEmail(String email) throws MessagingException;
    ResponseEntity<?> checkAuthEmail(MailDto mailDto);
}
