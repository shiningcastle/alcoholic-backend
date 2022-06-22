package someone.alcoholic.dto.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class MailDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z\\d]{8,16}$",
            message = "아이디는 영문자만 혹은 영문자 + 숫자 조합 형태여야 합니다. 길이는 8자에서 최대 16자까지 가능합니다.")
    private String id;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일은 필수 입력 값입니다.")
    private String email;
}
