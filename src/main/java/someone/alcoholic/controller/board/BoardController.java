package someone.alcoholic.controller.board;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.service.board.BoardService;
import someone.alcoholic.service.category.BoardCategoryService;
import someone.alcoholic.service.member.MemberService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;
    private final BoardCategoryService boardCategoryService;

    @Operation(summary = "게시판 조회", description = "특정 카테고리에 속하는 게시판 조회")
    @GetMapping("/boards/{boardCategory}")
    public ResponseEntity<ApiResult<Page<BoardDto>>> getBoards(@PathVariable String boardCategory, Pageable pageable) {
        Page<BoardDto> boards = boardService.getBoards(boardCategory, pageable);
        return ApiProvider.success(boards);
    }

    @Operation(summary = "특정 게시물 조회", description = "특정 seq에 해당하는 게시물 조회")
    @GetMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult<BoardDto>> getBoard(@PathVariable long boardSeq) {
        BoardDto boardDto = boardService.findBoardBySeq(boardSeq).convertToDto();
        return ApiProvider.success(boardDto);
    }

    @Operation(summary = "게시물 생성", description = "title, content, category를 입력해  새로운 게시물 생성")
    @Secured("ROLE_USER")
    @PostMapping("/board")
    public ResponseEntity<ApiResult<BoardDto>> addBoard(@RequestBody BoardInputDto boardInputDto, Principal principal) {
        Board board =  boardService.addBoard(principal.getName(), boardInputDto);
        return ApiProvider.success(board.convertToDto());
    }

    @Operation(summary = "게시물 수정", description = "title, content, category를 입력해 게시물을 수정한다.")
    @Secured("ROLE_USER")
    @PutMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult<BoardDto>> modifyBoard(@PathVariable long boardSeq, @Valid @RequestBody BoardInputDto boardInputDto, Principal principal) {
        Board board = boardService.modifyBoard(principal.getName(), boardSeq, boardInputDto);
        return ApiProvider.success(board.convertToDto());
    }

    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제한다.")
    @Secured("ROLE_USER")
    @DeleteMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> deleteBoard(Principal principal, @PathVariable long boardSeq) {
        boardService.deleteBoard(boardSeq, principal.getName());
        return ApiProvider.success();
    }
}
