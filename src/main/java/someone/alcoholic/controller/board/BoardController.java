package someone.alcoholic.controller.board;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.service.board.BoardService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시판 조회", description = "특정 카테고리에 속하는 게시판 조회")
    @GetMapping("/boards/{boardCategory}")
    public ResponseEntity<ApiResult<List<BoardDto>>> getBoards(HttpServletRequest request, @PathVariable String boardCategory, Pageable pageable) {
        return ApiProvider.success(boardService.getBoards(request, boardCategory, pageable));
    }

    @Operation(summary = "특정 게시물 조회", description = "특정 seq에 해당하는 게시물 조회")
    @Secured("ROLE_USER")
    @GetMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult<BoardDto>> getBoard(HttpServletRequest request, @PathVariable @Positive long boardSeq) {
        return ApiProvider.success(boardService.getBoard(request, boardSeq));
    }

    @Operation(summary = "게시물 생성", description = "title, content, category를 입력해  새로운 게시물 생성")
    @Secured("ROLE_USER")
    @PostMapping("/board")
    public ResponseEntity<ApiResult> addBoard(@RequestBody BoardInputDto boardInputDto, Principal principal) {
        boardService.addBoard(principal.getName(), boardInputDto);
        return ApiProvider.success();
    }

    @Operation(summary = "게시물 수정", description = "title, content, category를 입력해 게시물을 수정한다.")
    @Secured("ROLE_USER")
    @PutMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> modifyBoard(@PathVariable @Positive long boardSeq, @Valid @RequestBody BoardInputDto boardInputDto, Principal principal) {
        boardService.modifyBoard(principal.getName(), boardSeq, boardInputDto);
        return ApiProvider.success();
    }

    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제한다.")
    @Secured("ROLE_USER")
    @DeleteMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> deleteBoard(Principal principal, @PathVariable @Positive long boardSeq) {
        boardService.deleteBoard(boardSeq, principal.getName());
        return ApiProvider.success();
    }

}
