package someone.alcoholic.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Setter
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    private UUID id;
    private String memberId;
    private String refreshToken;

    public RefreshToken(UUID id, String memberId, String refreshToken) {
        this.id = id;
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
}
