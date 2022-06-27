package someone.alcoholic.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class MemberLoginDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z\\d]{8,16}$",
            message = "아이디는 영문자만 혹은 영문자 + 숫자 조합 형태여야 합니다. 길이는 8자에서 최대 16자까지 가능합니다.")
    private String id;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
            message = "비밀번호는 숫자, 영문자, 특수문자를 모두 포함해야 하며 길이는 8자에서 최대 16자 형식이어야 합니다.")
    private String password;

    public MemberLoginDto(String id, String password) {
        this.id = id;
        this.password = password;
    }

}
