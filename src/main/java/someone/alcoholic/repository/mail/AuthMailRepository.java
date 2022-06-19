package someone.alcoholic.repository.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.mail.AuthMail;
import someone.alcoholic.enums.MailType;

import java.util.Optional;

public interface AuthMailRepository extends JpaRepository<AuthMail, Long> {
    Optional<AuthMail> findByEmailAndType(String email, MailType type);
}
