package someone.alcoholic.domain.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class RefreshToken {

    private String tokenValue;
    private String memberId;
    private String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Builder
    public RefreshToken(String tokenValue, String memberId, String accessToken) {
        this.tokenValue = tokenValue;
        this.memberId = memberId;
        this.accessToken = accessToken;
    }
}