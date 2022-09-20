package someone.alcoholic.service.reply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.reply.Reply;
import someone.alcoholic.dto.reply.ReReplyInputDto;
import someone.alcoholic.dto.reply.ReplyDto;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.reply.ReplyInputDto;
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
    public Page<ReplyDto> getReplies(Pageable pageable, long boardSeq, String memberId) {
        log.info("댓글들 조회 시작");
        Board board = boardService.findBoardBySeq(boardSeq);
        Page<Reply> replies = replyRepository.findAllByBoardOrderByReplyParent(pageable, board);

        Member member = memberService.findMemberById(memberId);
        return replies.map(r -> r.convertReplyToDto(member.getNickname()));
    }

    @Override
    public long getRepliesNum(long boardSeq) {
        Board board = boardService.findBoardBySeq(boardSeq);
        return replyRepository.findAllByBoard(board).size();
    }

    @Override
    public ReplyDto addReReply(ReReplyInputDto replyInputDto, long boardSeq, String memberId) {
        log.info("대댓글 생성 시작");
        Board board = boardService.findBoardBySeq(boardSeq);
        Member member = memberService.findMemberById(memberId);
        Reply savedReply = replyRepository.save(
                Reply.convertInputReReplyDtoToReply(replyInputDto, board, member));
        savedReply.setReplyParent();
        log.info("대댓글 생성 완료 : reply={}", savedReply.getSeq());
        return savedReply.convertReplyToDto(member.getNickname());
    }

    @Override
    @Transactional
    public ReplyDto addReply(ReplyInputDto replyInputDto, long boardSeq, String memberId) {
        log.info("댓글 생성 시작");
        Board board = boardService.findBoardBySeq(boardSeq);
        Member member = memberService.findMemberById(memberId);
        Reply savedReply = replyRepository.save(
                Reply.convertInputReplyDtoToReply(replyInputDto, board, member));
        savedReply.setReplyParent();
        log.info("댓글 생성 완료 : reply={}", savedReply.getSeq());
        return savedReply.convertReplyToDto(member.getNickname());
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
        Member member = memberService.findMemberById(memberId);
        return reply.convertReplyToDto(member.getNickname());
    }

    @Override
    public void deleteReply(long replySeq) {
        log.info("댓글 삭제 시작 : reply={}", replySeq);
        Reply reply = findReplyBySeq(replySeq);
        replyRepository.delete(reply);
        log.info("댓글 삭제 성공 : reply={}", replySeq);
    }
}
