package someone.alcoholic.service.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import someone.alcoholic.dto.ReplyDto;
import someone.alcoholic.dto.ReplyInputDto;

public interface ReplyService {
    Page<ReplyDto> getReplies(Pageable pageable, int boardSeq);
    ReplyDto addReply(ReplyInputDto replyInputDto, int boardSeq, String memberId);
}
