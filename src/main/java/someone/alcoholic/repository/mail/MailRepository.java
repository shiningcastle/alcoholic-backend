package someone.alcoholic.repository.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.mail.Mail;
import someone.alcoholic.enums.MailType;

import java.util.Optional;

public interface MailRepository extends JpaRepository<Mail, Long> {
    boolean existsByEmail(String email);
    Optional<Mail> findTop1ByTypeAndEmailOrderByDate(MailType type, String email);
}
