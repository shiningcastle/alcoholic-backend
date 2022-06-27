package someone.alcoholic.domain.reply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.ReplyDto;
import someone.alcoholic.dto.ReplyInputDto;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private Long replyParent;
    private String content;
    private Boolean isRoot;

    @CreationTimestamp
    private Timestamp createdDate;
    private Timestamp updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_reply_board"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_reply_member"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public Reply(boolean isRoot, long replyParent, String content) {
        this.isRoot = isRoot;
        this.replyParent = replyParent;
        this.content = content;
    }

    public void setMember(Member member) {
        if(this.member != null) {
            this.member.getReplies().remove(this);
        }
        this.member = member;
        this.member.getReplies().add(this);

    }

    public void setBoard(Board board) {
        if(this.board != null) {
            this.board.getReplies().remove(this);
        }
        this.board = board;
        this.board.getReplies().add(this);
    }

    // 반드시 save한 이후에 사용해야함 (seq가 auto increment이기 때문)
    public void setReplyParent() {
        if(this.isRoot) {
            this.replyParent = this.seq;
        }
    }

    public static Reply convertInputDtoToReply(ReplyInputDto replyInputDto, Board board, Member member) {
        int replyParent = 0;
        if (!replyInputDto.getIsRoot()) {
            replyParent = replyInputDto.getReplyParent();
        }

        Reply reply =  new Reply(replyInputDto.getIsRoot(), replyParent, replyInputDto.getContent());
        reply.setBoard(board);
        reply.setMember(member);
        return reply;
    }

    public ReplyDto convertReplyToDto() {
        return new ReplyDto(this.seq, this.replyParent, this.content, this.isRoot, this.createdDate, this.updatedDate);
    }
}
