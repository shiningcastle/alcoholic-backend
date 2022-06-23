package someone.alcoholic.service.mail;

import org.springframework.http.ResponseEntity;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.MailType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

public interface MailService {
    ResponseEntity<ApiResult> sendEmail(MailType type, String email) throws MessagingException;
    void authEmail(MailType type, String email, int number, HttpServletResponse response);
    ResponseEntity<ApiResult> checkEmailCertified(MailType mailType, String email);
}
