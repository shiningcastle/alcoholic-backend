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

    @NotBlank(message = "난수의 내용이 없습니다.")
    private String number;

}
