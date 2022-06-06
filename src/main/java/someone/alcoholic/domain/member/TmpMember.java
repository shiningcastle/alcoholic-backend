package someone.alcoholic.domain.member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TmpMember {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long seq;

        @Column(nullable = false, unique = true)
        private String id;

        @Column(nullable = false)
        private String email;

        @Column(length = 15)
        @Enumerated(EnumType.STRING)
        private Provider provider;

        @Column(name = "created_date", nullable = false)
        @CreationTimestamp
        private Timestamp createdDate;

        public TmpMember(String id, String email, Provider provider) {
                this.id = id;
                this.email = email;
                this.provider = provider;
        }

        public Member convertToMember(String nickname) {
                return Member.builder()
                        .id(this.id)
                        .nickname(nickname)
                        .email(this.email)
                        .password(null)
                        .image("DefaultImage")
                        .role(Role.USER)
                        .provider(Provider.LOCAL).build();
        }
}
