package someone.alcoholic.domain.member;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import someone.alcoholic.domain.Board.Board;
import someone.alcoholic.dto.member.MemberDto;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@ToString(exclude = {})
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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String image;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "password_updated_date", nullable = false)
    @CreationTimestamp
    private Timestamp passwordUpdatedDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Board> boards;


    @Builder // oauth 유저 회원가입
    public Member(String id, String password, String nickname, String email, String image, Provider provider, Role role) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.image = image;
        this.provider = provider;
        this.role = role;
    }

    public static Member createLocalMember(String id, String password, String nickname, String email) {
        Member member = Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickname(nickname)
                .image("default_user.jpeg")
                .role(Role.USER)
                .provider(Provider.LOCAL).build();
        return member;
    }

    public MemberDto convertMemberDto() {
        return new MemberDto(this.nickname, this.email, this.image, this.role);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        setPasswordUpdatedDate(new Timestamp(System.currentTimeMillis()));
    }

    private void setPasswordUpdatedDate(Timestamp passwordUpdatedDate) {
        this.passwordUpdatedDate = passwordUpdatedDate;
    }
}
