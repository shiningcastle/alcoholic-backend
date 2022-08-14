package someone.alcoholic.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordDto {

    @ApiModelProperty(value = "신규 비밀번호", example = "newpassword123!", required = true)
    @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
            message = "새 비밀번호는 숫자, 영문자, 특수문자를 모두 포함해야 하며 길이는 8자에서 최대 16자 형식이어야 합니다.")
    private String newPassword;

}
