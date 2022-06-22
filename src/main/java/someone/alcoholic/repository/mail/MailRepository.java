package someone.alcoholic.repository.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.mail.Mail;

public interface MailRepository extends JpaRepository<Mail, Long> {
    boolean existsByEmail(String email);
}
