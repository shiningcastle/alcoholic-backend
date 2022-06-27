package someone.alcoholic.service.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.Board.Board;
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
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final BoardRepository boardRepository;
    private final BoardCategoryService boardCategoryService;
    private final MemberService memberService;

    @Override
    public Page<BoardDto> getBoards(String boardCategoryName, Pageable pageable) {
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardCategoryName);
        Page<Board> boards = boardRepository.findAllByBoardCategory(boardCategory, pageable);
        return boards.map(Board::convertToDto);
    }

    @Override
    public Board findBoardBySeq(long boardSeq) {
        return boardRepository.findById(boardSeq)
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.PAGE_NOT_FOUND));
    }

    @Override
    public Board addBoard(String memberId, BoardInputDto boardInputDto) {
        Member member = memberService.findMemberById(memberId);
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardInputDto.getCategory());

        Board board = Board.convertInputDtoToBoard(boardInputDto, member, boardCategory);
        return boardRepository.save(board);
    }

    @Override
    public Board modifyBoard(String memberId, long boardSeq, BoardInputDto boardInputDto) {
        Board board = this.findBoardBySeq(boardSeq);
        if (board.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(ExceptionEnum.USER_AND_WRITER_NOT_EQUAL);
        }
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardInputDto.getCategory());
        board.setBoardCategory(boardCategory);
        board.setContent(boardInputDto.getContent());
        board.setTitle(boardInputDto.getTitle());
        board.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return board;
    }

    @Override
    public void deleteBoard(long boardSeq, String memberId) {
        Member member = memberService.findMemberById(memberId);
        Board board = this.findBoardBySeq(boardSeq);
        if (member.equals(board.getMember())) {
            boardRepository.delete(board);
            return;
        }
        throw new CustomRuntimeException(ExceptionEnum.NOT_ALLOWED_ACCESS);
    }
}
