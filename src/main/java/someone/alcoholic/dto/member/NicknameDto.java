package someone.alcoholic.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
public class NicknameDto {

    @ApiModelProperty(name = "nickname", value = "닉네임", example = "1000 병의 소주를 마신 아저씨", required = true)
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Length(min = 2, max = 16)
    private String nickname;

    public NicknameDto(String nickname) {
        this.nickname = nickname;
    }
}
