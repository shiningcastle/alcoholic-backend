package someone.alcoholic.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import someone.alcoholic.enums.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class MemberDto {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Length(min = 2, max = 16)
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "이미지는 필수 입력 값입니다.")
    private String image;

    @NotBlank(message = "역할은 필수 입력 값입니다.")
    private Role role;

}
