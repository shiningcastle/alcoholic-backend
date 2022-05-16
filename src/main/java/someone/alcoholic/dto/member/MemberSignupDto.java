package someone.alcoholic.dto.member;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class MemberSignupDto {
    @NotBlank(message = "id - 빈칸 또는 공백을 허용하지 않습니다.")
    private String id;

    @NotBlank(message = "password - 빈칸 또는 공백을 허용하지 않습니다.")
    private String password;

    @NotBlank(message = "nickname - 빈칸 또는 공백을 허용하지 않습니다.")
    private String nickname;

    @Email(message = "email - 올바른 양식이 아닙니다.")
    private String email;
}
