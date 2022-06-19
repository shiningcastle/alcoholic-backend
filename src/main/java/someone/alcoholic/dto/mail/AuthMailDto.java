package someone.alcoholic.dto.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Getter
public class AuthMailDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "인증번호가 없습니다.")
    @Pattern(regexp = "[0123456789]{6}", message = "인증번호는 6자리 숫자입니다.")
    private String number;

}
