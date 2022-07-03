package someone.alcoholic.service.reply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.reply.Reply;
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
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardService boardService;
    private final MemberService memberService;

    public Reply findReplyBySeq(long replySeq) {
        log.info("댓글 조회 시작");
        return replyRepository.findById(replySeq)
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.REPLY_NOT_FOUND));
    }

    @Override
    public Page<ReplyDto> getReplies(Pageable pageable, long boardSeq) {
        log.info("댓글들 조회 시작");
        Board board = boardService.findBoardBySeq(boardSeq);
        Page<Reply> replies = replyRepository.findAllByBoardOrderByReplyParent(pageable, board);
        return replies.map(Reply::convertReplyToDto);

    }

    @Override
    @Transactional
    public ReplyDto addReply(ReplyInputDto replyInputDto, long boardSeq, String memberId) {
        log.info("댓글 생성 시작");
        Board board = boardService.findBoardBySeq(boardSeq);
        Member member = memberService.findMemberById(memberId);
        Reply savedReply = replyRepository.save(
                Reply.convertInputDtoToReply(replyInputDto, board, member));
        savedReply.setReplyParent();
        log.info("댓글 생성 완료 : reply={}", savedReply.getSeq());
        return savedReply.convertReplyToDto();
    }

    @Override
    @Transactional
    public ReplyDto modifyReply(ReplyInputDto replyInputDto, String memberId, long replySeq) {
        log.info("댓글 수정 시작 : member={}, reply={}", memberId, replySeq);
        Reply reply = findReplyBySeq(replySeq);

        if (!reply.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(ExceptionEnum.USER_AND_WRITER_NOT_EQUAL);
        }
        reply.setContent(replyInputDto.getContent());
        reply.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        log.info("댓글 수정 성공 : member={}, reply={}", memberId, replySeq);
        return reply.convertReplyToDto();
    }

    @Override
    public void deleteReply(long replySeq) {
        log.info("댓글 삭제 시작 : reply={}", replySeq);
        Reply reply = findReplyBySeq(replySeq);
        replyRepository.delete(reply);
        log.info("댓글 삭제 성공 : reply={}", replySeq);
    }
}
