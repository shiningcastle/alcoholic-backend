package someone.alcoholic.dto.reply;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ReplyDto {

    @ApiModelProperty(name = "seq", value = "댓글번호", example = "132")
    private Long seq;

    @ApiModelProperty(name = "replyParent", value = "상위 댓글번호", example = "13")
    private Long replyParent;

    @ApiModelProperty(name = "content", value = "댓글내용", example = "오 너무 좋은 글이네요ㅎㅎ 잘봤습니다!")
    private String content;

    @ApiModelProperty(name = "isRoot", value = "최상위 댓글 여부", example = "true")
    private Boolean isRoot;

    @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp createdDate;

    @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
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
