package someone.alcoholic.domain.mail;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import someone.alcoholic.enums.BoolEnum;
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
    private int number;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp sendDate;

    private Timestamp authDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoolEnum completion;

    @Builder
    public Mail(Long seq, String email, MailType type, int number, Timestamp sendDate, Timestamp authDate, BoolEnum completion) {
        this.seq = seq;
        this.email = email;
        this.type = type;
        this.number = number;
        this.sendDate = sendDate;
        this.authDate = authDate;
        this.completion = completion;
    }

    public void recordAuthDate(Timestamp authDate) {
        this.authDate = authDate;
    }

    public void changeCompletion(BoolEnum completion) {
        this.completion = completion;
    }
}
