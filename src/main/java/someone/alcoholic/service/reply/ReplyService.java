package someone.alcoholic.service.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import someone.alcoholic.dto.ReplyDto;
import someone.alcoholic.dto.ReplyInputDto;

public interface ReplyService {
    Page<ReplyDto> getReplies(Pageable pageable, long boardSeq);
    ReplyDto addReply(ReplyInputDto replyInputDto, long boardSeq, String memberId);
    ReplyDto modifyReply(ReplyInputDto replyInputDto, String memberId, long replySeq);
    void deleteReply(long replySeq);
}
