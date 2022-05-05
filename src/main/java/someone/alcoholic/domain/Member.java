package someone.alcoholic.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(length = 45, nullable = false)
    private String password;

    @Column(length = 45, nullable = false)
    private String nickname;

    @Column(length = 15)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(name = "password_updated_date")
    private Timestamp passwordUpdatedDate;
}
