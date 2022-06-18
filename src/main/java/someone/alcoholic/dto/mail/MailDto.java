package someone.alcoholic.dto.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
public class MailDto {
    @Email
    private String email;

    @NotBlank(message = "인증번호가 없습니다.")
    private String number;

}
