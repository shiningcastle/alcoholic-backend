package someone.alcoholic.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(length = 100, nullable = false)     // 인코딩 하기 때문에 더 커야됨
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
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "password_updated_date")
    @CreationTimestamp
    private Timestamp passwordUpdatedDate;

    public Member(String email, String password, String nickname,
                  Provider provider, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.role = role;
        // 3개월 뒤로 설정
    }

    public static Member createLocalMember(String email, String password, String nickname) {
        Member member = new Member(email, password, nickname, Provider.LOCAL, Role.USER);
        member.passwordUpdatedDate = Timestamp.valueOf(LocalDateTime.now().plusMonths(3));
        return member;
    }
}
