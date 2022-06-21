package someone.alcoholic.service.mail;

import org.springframework.http.ResponseEntity;
import someone.alcoholic.enums.MailType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

public interface MailService {
    ResponseEntity<?> sendAuthEmail(MailType type, String email) throws MessagingException;
    ResponseEntity<?> checkAuthEmail(MailType type, String email, int number, HttpServletResponse response);
}
