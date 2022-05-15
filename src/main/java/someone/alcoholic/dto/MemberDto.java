package someone.alcoholic.dto;

import javax.validation.constraints.NotBlank;

public class MemberDto {
    @NotBlank(message = "E-Mail 빈(공백) 문자열을 허용하지 않습니다.")
    private String email;

    @NotBlank(message = "nickname 빈(공백) 문자열을 허용하지 않습니다.")
    private String nickname;
}
