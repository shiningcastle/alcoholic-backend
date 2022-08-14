package someone.alcoholic.service.heart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.heart.Heart;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.heart.HeartRepository;
import someone.alcoholic.service.board.BoardService;
import someone.alcoholic.service.member.MemberService;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

    private final HeartRepository heartRepository;
    private final BoardService boardService;
    private final MemberService memberService;

    public void saveBoardHeart(Principal principal, Long boardSeq) {
        Board board = boardService.findBoardBySeq(boardSeq);
        Member member = board.getMember();
        String memberId = member.getId();
        log.info("{} 유저 - {} 게시글 좋아요 등록 요청", memberId, boardSeq);
        memberService.checkAuthorizedUser(principal, member.getId());
        if (heartRepository.existsByMemberAndBoard(member, board)) {
            log.info("{} 유저 -{} 게시글 좋아요 등록 실패 - 이미 좋아요 등록된 유저", memberId, boardSeq);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.HEART_ALREADY_EXISTS);
        }
        Heart heart = Heart.builder().member(member).board(board).build();
        heartRepository.save(heart);
        log.info("{} 유저 - {} 게시글 좋아요 등록 성공", memberId, boardSeq);
    }

    public void deleteBoardHeart(Principal principal, Long boardSeq) {
        Board board = boardService.findBoardBySeq(boardSeq);
        Member member = board.getMember();
        String memberId = member.getId();
        log.info("{} 유저 - {} 게시글 좋아요 삭제 요청", memberId, boardSeq);
        memberService.checkAuthorizedUser(principal, member.getId());
        if (!heartRepository.existsByMemberAndBoard(member, board)) {
            log.info("{} 유저 -{} 게시글 좋아요 삭제 실패 - 좋아요 등록하지 않은 유저", memberId, boardSeq);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.HEART_NOT_EXISTS);
        }
        heartRepository.deleteById(boardSeq);
        log.info("{} 유저 - {} 게시글 좋아요 삭제 성공", memberId, boardSeq);
    }
}
