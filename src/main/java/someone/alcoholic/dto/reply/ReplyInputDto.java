package someone.alcoholic.dto.reply;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ReplyInputDto {
    private boolean isRoot;
    private int replyParent;
    @NotBlank(message = "본문은 필수 입력 값입니다.")
    private String content;

    public ReplyInputDto(boolean isRoot, int replyParent, String content) {
        this.isRoot = isRoot;
        this.replyParent = replyParent;
        this.content = content;
    }
}
