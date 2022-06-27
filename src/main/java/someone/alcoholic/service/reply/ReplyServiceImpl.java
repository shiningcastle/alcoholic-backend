package someone.alcoholic.service.reply;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.Board.Board;
import someone.alcoholic.domain.Reply.Reply;
import someone.alcoholic.dto.ReplyDto;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.ReplyInputDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.reply.ReplyRepository;
import someone.alcoholic.service.board.BoardService;
import someone.alcoholic.service.member.MemberService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardService boardService;
    private final MemberService memberService;

    public Reply findReplyBySeq(long replySeq) {
        return replyRepository.findById(replySeq)
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.REPLY_NOT_FOUND));
    }

    @Override
    public Page<ReplyDto> getReplies(Pageable pageable, long boardSeq) {
        Board board = boardService.findBoardBySeq(boardSeq);
        Page<Reply> replies = replyRepository.findAllByBoardOrderByReplyParent(pageable, board);
        return replies.map(Reply::convertReplyToDto);

    }

    @Override
    @Transactional
    public ReplyDto addReply(ReplyInputDto replyInputDto, long boardSeq, String memberId) {
        Board board = boardService.findBoardBySeq(boardSeq);
        Member member = memberService.findMemberById(memberId);
        Reply savedReply = replyRepository.save(
                Reply.convertInputDtoToReply(replyInputDto, board, member));
        savedReply.setReplyParent();
        return savedReply.convertReplyToDto();
    }

    @Override
    @Transactional
    public ReplyDto modifyReply(ReplyInputDto replyInputDto, String memberId, long relySeq) {
        Reply reply = findReplyBySeq(relySeq);

        if (reply.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(ExceptionEnum.USER_AND_WRITER_NOT_EQUAL);
        }
        reply.setContent(replyInputDto.getContent());
        reply.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return reply.convertReplyToDto();
    }

    @Override
    public void deleteReply(long replySeq) {
        Reply reply = findReplyBySeq(replySeq);
        replyRepository.delete(reply);
    }
}
