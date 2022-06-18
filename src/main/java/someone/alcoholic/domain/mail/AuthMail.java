package someone.alcoholic.domain.mail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity(name = "auth_mail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthMail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false, unique = true)
    private String email;
}
