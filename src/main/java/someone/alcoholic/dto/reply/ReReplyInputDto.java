package someone.alcoholic.dto.reply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ReReplyInputDto {

    @ApiModelProperty(name = "isRoot", value = "최상위 댓글 여부", example = "true")
    private boolean isRoot;

    @ApiModelProperty(name = "replyParent", value = "부모(루트) 댓글 번호", example = "1")
    private int replyParent;

    @ApiModelProperty(name = "content", value = "댓글 내용", example = "오 너무 좋은 글이네요ㅎㅎ 잘봤습니다!")
    @NotBlank(message = "본문은 필수 입력 값입니다.")
    private String content;

    public ReReplyInputDto(boolean isRoot, int replyParent, String content) {
        this.isRoot = isRoot;
        this.replyParent = replyParent;
        this.content = content;
    }
}
