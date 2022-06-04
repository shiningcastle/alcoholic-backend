package someone.alcoholic.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.Board.Board;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.service.board.BoardService;
import someone.alcoholic.service.category.BoardCategoryService;
import someone.alcoholic.service.member.MemberService;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;
    private final BoardCategoryService boardCategoryService;

    @GetMapping("/boards/{boardCategory}")
    public ApiResult<Page<BoardDto>> getBoards(@PathVariable String boardCategory, Pageable pageable) {
        Page<BoardDto> boards = boardService.getBoards(boardCategory, pageable);
        return ApiProvider.success(boards);
    }

    @GetMapping("/board/{boardId}")
    public ApiResult<BoardDto> getBoard(@PathVariable int boardSeq) {
        BoardDto boardDto = boardService.getBoard(boardSeq).convertToDto();
        return ApiProvider.success(boardDto);
    }

    @PostMapping("/board")
    public ApiResult<Board> addBoard(@RequestBody BoardInputDto boardInputDto, @AuthenticationPrincipal Principal principal) {
        Board board =  boardService.addBoard(principal.getName(), boardInputDto);
        return ApiProvider.success(board);
    }

    @DeleteMapping("/board/{boardId}")
    public ApiResult<Board> deleteBoard(@AuthenticationPrincipal Principal principal, @PathVariable int boardId) {
        boardService.deleteBoard(boardId, principal.getName());
        return ApiProvider.success();
    }
}
