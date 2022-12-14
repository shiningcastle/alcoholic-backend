package someone.alcoholic.dto.reply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ReplyInputDto {
    @ApiModelProperty(name = "content", value = "댓글 내용", example = "오 너무 좋은 글이네요ㅎㅎ 잘봤습니다!")
    @NotBlank(message = "본문은 필수 입력 값입니다.")
    private String content;

    public ReplyInputDto(String content) {
        this.content = content;
    }
}
