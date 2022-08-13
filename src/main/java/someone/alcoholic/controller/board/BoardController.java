package someone.alcoholic.controller.board;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.board.BoardService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
    public ResponseEntity<ApiResult<List<BoardDto>>> getBoards(HttpServletRequest request,
               @PathVariable @NotEmpty @ApiParam(value = "카테고리 명", required = true, example = "주류 할인 정보") String boardCategory,
                                                               Pageable pageable, Principal principal) {
        return ApiProvider.success(boardService.getBoards(request, boardCategory, pageable, principal));
    }

    @Operation(summary = "특정 게시물 조회", description = "특정 글번호에 해당하는 게시물 조회")
    @GetMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult<BoardDto>> getBoard(HttpServletRequest request,
                                                        @PathVariable @Positive @ApiParam(value = "글번호", required = true, example = "13") long boardSeq) {
        return ApiProvider.success(boardService.getBoard(request, boardSeq));
    }

    @Operation(summary = "게시물 생성 (인증 필요)", description = "제목, 내용, 카테고리를 받아 게시물 생성")
    @Secured("ROLE_USER")
    @PostMapping("/board")
    public ResponseEntity<ApiResult<BoardDto>> addBoard(@RequestBody @Valid @ApiParam(value = "게시물 생성 정보", required = true) BoardInputDto boardInputDto, Principal principal) {
        return ApiProvider.success(boardService.addBoard(principal.getName(), boardInputDto), MessageEnum.BOARD_INSERT_SUCCESS);
    }

    @Operation(summary = "게시물 수정 (인증 필요)", description = "제목, 내용, 카테고리를 받아 게시물을 수정")
    @Secured("ROLE_USER")
    @PutMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult<BoardDto>> modifyBoard(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long boardSeq,
                                                 @Valid @RequestBody @ApiParam(value = "게시물 수정 정보", required = true) BoardInputDto boardInputDto, Principal principal) {
        return ApiProvider.success(boardService.modifyBoard(principal.getName(), boardSeq, boardInputDto), MessageEnum.BOARD_UPDATE_SUCCESS);
    }

    @Operation(summary = "게시물 삭제 (인증 필요)", description = "특정 게시물을 삭제한다.")
    @Secured("ROLE_USER")
    @DeleteMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> deleteBoard(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long boardSeq,
                                                 Principal principal) {
        boardService.deleteBoard(boardSeq, principal.getName());
        return ApiProvider.success(MessageEnum.BOARD_DELETE_SUCCESS);
    }

}
