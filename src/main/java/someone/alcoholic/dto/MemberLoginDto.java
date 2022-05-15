package someone.alcoholic.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class MemberLoginDto {

    @NotBlank(message = "E-Mail 빈(공백) 문자열을 허용하지 않습니다.")
    private String email;

    @NotBlank(message = "password 빈(공백) 문자열을 허용하지 않습니다.")
    private String password;
}
