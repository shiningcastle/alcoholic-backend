package someone.alcoholic.repository.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.mail.Mail;
import someone.alcoholic.enums.BoolEnum;
import someone.alcoholic.enums.MailType;

import java.util.Optional;

public interface MailRepository extends JpaRepository<Mail, Long> {
    Optional<Mail> findTop1ByEmailAndTypeAndCompletionOrderByAuthDateDesc(String email, MailType type, BoolEnum completion);
    Optional<Mail> findByEmailAndTypeAndNumberAndCompletion(String email, MailType type, int number, BoolEnum completion);
}
