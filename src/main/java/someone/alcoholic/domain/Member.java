package someone.alcoholic.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private int id;
    private String email;
    private String password;
    private String nickname;
    private String provider;
    private String role;
    private Timestamp createdDate;
    private Timestamp passwordUpdatedDate;
}
