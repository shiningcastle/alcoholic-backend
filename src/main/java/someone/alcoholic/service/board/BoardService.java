package someone.alcoholic.service.board;

import org.springframework.data.domain.Pageable;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BoardService {
    List<BoardDto> getBoards(HttpServletRequest request, String boardCategoryName, Pageable pageable);
    BoardDto getBoard(HttpServletRequest request, long boardSeq);
    Board findBoardBySeq(long boardSeq);
    void addBoard(String memberId, BoardInputDto boardInputDto);
    void modifyBoard(String memberId, long boardSeq, BoardInputDto boardInputDto);
    void deleteBoard(long boardSeq, String memberId);
}
