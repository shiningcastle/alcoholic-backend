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
    public Board getBoard(int boardSeq) {
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
    public void deleteBoard(int boardSeq, String memberId) {
        Member member = memberService.findMemberById(memberId);
        Board board = getBoard(boardSeq);
        if (member.equals(board.getMember())) {
            boardRepository.delete(board);
        }
        throw new CustomRuntimeException(ExceptionEnum.NOT_ALLOWED_ACCESS);
    }
}
