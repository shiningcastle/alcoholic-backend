package someone.alcoholic.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class newPasswordResetDto extends NewPasswordDto {

    @ApiModelProperty(name = "email", value = "이메일", example = "test1234@google.com")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    public newPasswordResetDto(String newPassword, String email) {
        super(newPassword);
        this.email = email;
    }

}
