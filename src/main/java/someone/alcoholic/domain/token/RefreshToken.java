package someone.alcoholic.domain.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class RefreshToken {

    private String value;
    private String memberId;
    private String accessToken;

    @Builder
    public RefreshToken(String value, String memberId, String accessToken) {
        this.value = value;
        this.memberId = memberId;
        this.accessToken = accessToken;
    }
}