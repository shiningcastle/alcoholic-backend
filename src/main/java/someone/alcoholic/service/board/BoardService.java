package someone.alcoholic.service.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import someone.alcoholic.domain.Board.Board;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;

public interface BoardService {
    Page<BoardDto> getBoards(String boardCategoryName, Pageable pageable);
    Board getBoard(int boardSeq);
    Board addBoard(String memberId, BoardInputDto boardInputDto);
    void deleteBoard(int boardSeq, String memberId);
}
