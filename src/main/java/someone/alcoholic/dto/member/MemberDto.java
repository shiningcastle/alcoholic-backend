package someone.alcoholic.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import someone.alcoholic.enums.Role;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class MemberDto {
    @NotBlank(message = "nickname 빈(공백) 문자열을 허용하지 않습니다.")
    private String nickname;

    @NotBlank(message = "E-Mail 빈(공백) 문자열을 허용하지 않습니다.")
    private String email;

    @NotBlank(message = "image 빈(공백) 문자열을 허용하지 않습니다.")
    private String image;

    @NotBlank(message = "role 빈(공백) 문자열을 허용하지 않습니다.")
    private Role role;
}
