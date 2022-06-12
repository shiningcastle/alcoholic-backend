package someone.alcoholic.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
public class OAuthSignupDto {
    @NotBlank(message = "nickname - 빈칸 또는 공백을 허용하지 않습니다.")
    private String nickname;

    public OAuthSignupDto(String nickname) {
        this.nickname = nickname;
    }
}
