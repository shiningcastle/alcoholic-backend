package someone.alcoholic.dto.member;

import lombok.Getter;

import javax.validation.constraints.NotBlank;


@Getter
public class OAuthSignupDto {
    @NotBlank(message = "nickname - 빈칸 또는 공백을 허용하지 않습니다.")
    private String nickname;
}
