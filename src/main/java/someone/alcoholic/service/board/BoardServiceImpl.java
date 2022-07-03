package someone.alcoholic.service.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.board.BoardRepository;
import someone.alcoholic.service.category.BoardCategoryService;
import someone.alcoholic.service.member.MemberService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final BoardRepository boardRepository;
    private final BoardCategoryService boardCategoryService;
    private final MemberService memberService;

    @Override
    public Page<BoardDto> getBoards(String boardCategoryName, Pageable pageable) {
        log.info("boards 조회 시작");
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardCategoryName);
        Page<Board> boards = boardRepository.findAllByBoardCategory(boardCategory, pageable);
        return boards.map(Board::convertToDto);
    }

    @Override
    public Board findBoardBySeq(long boardSeq) {
        log.info("board {} 조회 시작", boardSeq);
        return boardRepository.findById(boardSeq)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.PAGE_NOT_FOUND));
    }

    @Override
    public Board addBoard(String memberId, BoardInputDto boardInputDto) {
        log.info("member {}의 board 생성 시작", memberId);
        Member member = memberService.findMemberById(memberId);
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardInputDto.getCategory());

        Board board = Board.convertInputDtoToBoard(boardInputDto, member, boardCategory);
        log.info("member {}의 board {} 생성 완료", memberId, board.getSeq());
        return boardRepository.save(board);
    }

    @Override
    public Board modifyBoard(String memberId, long boardSeq, BoardInputDto boardInputDto) {
        log.info("member {}의 board {} 수정 시작", memberId, boardSeq);
        Board board = this.findBoardBySeq(boardSeq);
        if (!board.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(ExceptionEnum.USER_AND_WRITER_NOT_EQUAL);
        }
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardInputDto.getCategory());
        board.setBoardCategory(boardCategory);
        board.setContent(boardInputDto.getContent());
        board.setTitle(boardInputDto.getTitle());
        board.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        log.info("member {}의 board {} 수정 완료", memberId, boardSeq);
        return board;
    }

    @Override
    public void deleteBoard(long boardSeq, String memberId) {
        log.info("member {}의 board {} 삭제 시작", memberId, boardSeq);
        Member member = memberService.findMemberById(memberId);
        Board board = this.findBoardBySeq(boardSeq);
        if (member.equals(board.getMember())) {
            boardRepository.delete(board);
            log.info("member {}의 board {} 삭제 완료", memberId, boardSeq);
            return;
        }
        throw new CustomRuntimeException(ExceptionEnum.NOT_ALLOWED_ACCESS);
    }
}
