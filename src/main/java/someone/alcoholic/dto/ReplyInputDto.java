package someone.alcoholic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyInputDto {
    private Boolean isRoot;
    private int replyParent;
    private String content;

    public ReplyInputDto(Boolean isRoot, int replyParent, String content) {
        this.isRoot = isRoot;
        this.replyParent = replyParent;
        this.content = content;
    }
}
