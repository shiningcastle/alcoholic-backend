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
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailType type;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp date;

    @Builder
    public Mail(Long seq, String email, MailType type, Timestamp date) {
        this.seq = seq;
        this.email = email;
        this.type = type;
        this.date = date;
    }
}
