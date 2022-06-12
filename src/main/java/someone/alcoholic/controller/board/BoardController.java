package someone.alcoholic.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
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

    @GetMapping("/board/{boardSeq}")
    public ApiResult<BoardDto> getBoard(@PathVariable int boardSeq) {
        BoardDto boardDto = boardService.getBoard(boardSeq).convertToDto();
        return ApiProvider.success(boardDto);
    }

    @Secured("ROLE_USER")
    @PostMapping("/board")
    public ApiResult<Board> addBoard(@RequestBody BoardInputDto boardInputDto, Principal principal) {
        Board board =  boardService.addBoard(principal.getName(), boardInputDto);
        return ApiProvider.success(board);
    }

    @Secured("ROLE_USER")
    @PutMapping("/board/{boardSeq}")
    public ApiResult<Board> modifyBoard(@PathVariable int boardSeq, @RequestBody BoardInputDto boardInputDto, Principal principal) {
        Board board = boardService.modifyBoard(principal.getName(), boardSeq, boardInputDto);
        return ApiProvider.success(board);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/board/{boardSeq}")
    public ApiResult<Board> deleteBoard(Principal principal, @PathVariable int boardSeq) {
        boardService.deleteBoard(boardSeq, principal.getName());
        return ApiProvider.success();
    }
}
