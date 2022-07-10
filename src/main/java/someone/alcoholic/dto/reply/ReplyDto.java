package someone.alcoholic.dto.reply;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ReplyDto {
    private Long seq;
    private Long replyParent;
    private String content;
    private Boolean isRoot;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    public ReplyDto(Long seq, Long replyParent, String content, Boolean isRoot, Timestamp createdDate, Timestamp updatedDate) {
        this.seq = seq;
        this.replyParent = replyParent;
        this.content = content;
        this.isRoot = isRoot;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
