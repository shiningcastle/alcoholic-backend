package someone.alcoholic.controller.board;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.dto.board.BoardUpdateDto;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.board.BoardService;

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
    @GetMapping("/boards")
    public ResponseEntity<ApiResult<List<BoardDto>>> getBoards(@RequestParam(value = "category") @Positive @ApiParam(value = "카테고리 번호", required = true, example = "1") long categorySeq,
                                                               Pageable pageable, Principal principal) {
        return ApiProvider.success(boardService.getBoards(principal, categorySeq, pageable));
    }

    @Operation(summary = "특정 게시물 조회", description = "특정 글번호에 해당하는 게시물 조회")
    @GetMapping("/board/{seq}")
    public ResponseEntity<ApiResult<BoardDto>> getBoard(@PathVariable @Positive @ApiParam(value = "글번호", required = true, example = "13") long seq, Principal principal) {
        return ApiProvider.success(boardService.getBoard(principal, seq));
    }

    @Operation(summary = "게시물 생성 (인증 필요)", description = "제목, 내용, 카테고리를 받아 게시물 생성")
    @Secured("ROLE_USER")
    @PostMapping("/board")
    public ResponseEntity<ApiResult<BoardDto>> addBoard(@RequestPart(value = "json") @Valid @ApiParam(value = "게시물 생성 정보", required = true) BoardInputDto boardInputDto, @RequestPart(value = "file", required = false) List<MultipartFile> fileList, Principal principal) {
        return ApiProvider.success(boardService.addBoard(principal, boardInputDto, fileList), MessageEnum.BOARD_INSERT_SUCCESS);
    }

    @Operation(summary = "게시물 수정 (인증 필요)", description = "제목, 내용, 카테고리를 받아 게시물을 수정")
    @Secured("ROLE_USER")
    @PutMapping("/board/{seq}")
    public ResponseEntity<ApiResult<BoardDto>> modifyBoard(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long seq,
                                                           @RequestPart(value = "json") @Valid @ApiParam(value = "게시물 수정 정보", required = true) BoardUpdateDto boardUpdateDto,
                                                           @RequestPart(value = "file", required = false) List<MultipartFile> fileList, Principal principal) {
        return ApiProvider.success(boardService.modifyBoard(principal, seq, boardUpdateDto, fileList), MessageEnum.BOARD_UPDATE_SUCCESS);
    }

    @Operation(summary = "게시물 삭제 (인증 필요)", description = "특정 게시물을 삭제한다.")
    @Secured("ROLE_USER")
    @DeleteMapping("/board/{seq}")
    public ResponseEntity<ApiResult> deleteBoard(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long seq,
                                                 Principal principal) {
        boardService.deleteBoard(principal, seq);
        return ApiProvider.success(MessageEnum.BOARD_DELETE_SUCCESS);
    }

}
