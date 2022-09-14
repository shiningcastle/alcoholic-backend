package someone.alcoholic.service.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import someone.alcoholic.dto.reply.ReReplyInputDto;
import someone.alcoholic.dto.reply.ReplyDto;
import someone.alcoholic.dto.reply.ReplyInputDto;

public interface ReplyService {
    Page<ReplyDto> getReplies(Pageable pageable, long boardSeq);
    long getRepliesNum(long boardSeq);
    ReplyDto addReReply(ReReplyInputDto replyInputDto, long boardSeq, String memberId);
    ReplyDto addReply(ReplyInputDto replyInputDto, long boardSeq, String memberId);
    ReplyDto modifyReply(ReplyInputDto replyInputDto, String memberId, long replySeq);
    void deleteReply(long replySeq);
}
