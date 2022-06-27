package someone.alcoholic.dto.member;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
public class OAuthSignupDto {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Length(min = 2, max = 16)
    private String nickname;

    public OAuthSignupDto(String nickname) {
        this.nickname = nickname;
    }
}
