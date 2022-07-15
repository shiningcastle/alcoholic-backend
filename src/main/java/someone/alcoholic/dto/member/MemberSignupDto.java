package someone.alcoholic.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignupDto {

    @ApiModelProperty(value = "아이디", example = "test1234", required = true)
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z\\d]{8,16}$",
            message = "아이디는 영문자만 혹은 영문자 + 숫자 조합 형태여야 합니다. 길이는 8자에서 최대 16자까지 가능합니다.")
    private String id;

    @ApiModelProperty(value = "비밀번호", example = "password1234!", required = true)
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
            message = "비밀번호는 숫자, 영문자, 특수문자를 모두 포함해야 하며 길이는 8자에서 최대 16자 형식이어야 합니다.")
    private String password;

//    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
//    @Length(min = 2, max = 16)
//    private String nickname;

    @ApiModelProperty(value = "이메일", example = "test1234@google.com", required = true)
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    public MemberSignupDto(String id, String password, String email) {
        this.id = id;
        this.password = password;
//        this.nickname = nickname;
        this.email = email;
    }
}
