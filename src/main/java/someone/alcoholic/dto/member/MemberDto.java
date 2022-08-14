package someone.alcoholic.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import someone.alcoholic.enums.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class MemberDto {

    @ApiModelProperty(name = "nickname", value = "닉네임", example = "1000 병의 소주를 마신 아저씨")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Length(min = 2, max = 16)
    private String nickname;

    @ApiModelProperty(name = "email", value = "이메일", example = "test1234@google.com")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @ApiModelProperty(name = "image", value = "이미지 파일 경로", example = "https://alcoholics3.s3.ap-northeast-2.amazonaws.com/member/default.jpg")
    @NotBlank(message = "이미지는 필수 입력 값입니다.")
    private String image;

    @ApiModelProperty(name = "role", value = "역할", example = "USER")
    @NotBlank(message = "역할은 필수 입력 값입니다.")
    private Role role;

}
