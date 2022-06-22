package someone.alcoholic.service.mail;

import org.springframework.http.ResponseEntity;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.mail.MailDto;
import someone.alcoholic.enums.MailType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

public interface MailService {
    ResponseEntity<ApiResult> sendAuthEmail(MailType type, MailDto mailDto) throws MessagingException;
    void checkAuthEmail(MailType type, String email, int number, HttpServletResponse response);
    void checkEmailCertified(String email);
}
