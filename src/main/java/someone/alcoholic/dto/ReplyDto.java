package someone.alcoholic.dto;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ReplyDto {
    private int seq;
    private int replyParent;
    private String content;
    private Boolean isRoot;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    public ReplyDto(int seq, int replyParent, String content, Boolean isRoot, Timestamp createdDate, Timestamp updatedDate) {
        this.seq = seq;
        this.replyParent = replyParent;
        this.content = content;
        this.isRoot = isRoot;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
