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
import someone.alcoholic.repository.ReplyRepository;
import someone.alcoholic.service.board.BoardService;
import someone.alcoholic.service.member.MemberService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardService boardService;
    private final MemberService memberService;

    @Override
    public Page<ReplyDto> getReplies(Pageable pageable, int boardSeq) {
        Board board = boardService.findBoardBySeq(boardSeq);
        Page<Reply> replies = replyRepository.findAllByBoardOrderByReplyParent(pageable, board);
        return replies.map(Reply::convertReplyToDto);

    }

    @Override
    @Transactional
    public ReplyDto addReply(ReplyInputDto replyInputDto, int boardSeq, String memberId) {
        Board board = boardService.findBoardBySeq(boardSeq);
        Member member = memberService.findMemberById(memberId);
        Reply savedReply = replyRepository.save(
                Reply.convertInputDtoToReply(replyInputDto, board, member));
        savedReply.setReplyParent();
        return savedReply.convertReplyToDto();

    }
}
