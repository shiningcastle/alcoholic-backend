package someone.alcoholic.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    private int id;
    private String email;
    private String password;
    private String nickname;
    private String provider;
    private String role;
    private Timestamp createdDate;
    private Timestamp passwordUpdatedDate;

    public Member(String email, String password, String nickname, String provider, String role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.role = role;
    }
}
