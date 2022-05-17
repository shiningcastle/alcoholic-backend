package someone.alcoholic.domain.member;

import lombok.*;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(length = 100, nullable = false)     // 인코딩 하기 때문에 더 커야됨
    private String password;

    @Column(length = 45, nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String image;

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

    @Builder // oauth 유저 회원가입
    public Member(String id, String password, String nickname, String email, String image, Provider provider) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.image = image;
        this.provider = provider;
        this.role = role;
        // 3개월 뒤로 설정
    }

    public static Member createLocalMember(String id, String password, String nickname, String email) {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .image("")
                .provider(Provider.LOCAL).build();
        member.passwordUpdatedDate = Timestamp.valueOf(LocalDateTime.now().plusMonths(3));
        return member;
    }

    public Member update(String nickname) {
        this.nickname = nickname;
        return this;
    }
}
