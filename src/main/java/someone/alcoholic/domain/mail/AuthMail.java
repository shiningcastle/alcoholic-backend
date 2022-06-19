package someone.alcoholic.domain.mail;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import someone.alcoholic.enums.MailType;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Entity(name = "auth_mail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthMail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailType type;

    @Column(name = "last_date", nullable = false)
    @CreationTimestamp
    private Timestamp lastDate;

    @Builder
    public AuthMail(Long seq, String email, MailType type, Timestamp lastDate) {
        this.seq = seq;
        this.email = email;
        this.type = type;
        this.lastDate = lastDate;
    }

    public void setLastDate(Timestamp lastDate) {
        this.lastDate = lastDate;
    }
}
