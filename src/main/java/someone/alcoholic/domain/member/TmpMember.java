package someone.alcoholic.domain.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
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
        @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private Timestamp createdDate;

        public TmpMember(String id, String email, Provider provider) {
                this.id = id;
                this.email = email;
                this.provider = provider;
        }

        public Member convertToMember(String nickname, String image) {
                return Member.builder()
                        .id(this.id)
                        .nickname(nickname)
                        .email(this.email)
                        .password(null)
                        .image(image)
                        .role(Role.USER)
                        .provider(Provider.LOCAL).build();
        }
}
