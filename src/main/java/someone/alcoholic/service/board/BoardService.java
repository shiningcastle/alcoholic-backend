package someone.alcoholic.service.board;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.dto.board.BoardUpdateDto;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

public interface BoardService {
    List<BoardDto> getBoards(Principal principal, long categorySeq, Pageable pageable);
    BoardDto getBoard(Principal principal, long boardSeq);
    Board findBoardBySeq(long boardSeq);
    BoardDto addBoard(Principal principal, BoardInputDto boardInputDto, List<MultipartFile> fileList);
    BoardDto modifyBoard(Principal principal, long boardSeq, BoardUpdateDto boardUpdateDto, List<MultipartFile> fileList);
    void deleteBoard(Principal principal, long boardSeq);
}
