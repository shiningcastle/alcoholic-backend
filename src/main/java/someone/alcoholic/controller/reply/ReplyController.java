package someone.alcoholic.controller.reply;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.reply.ReplyDto;
import someone.alcoholic.dto.reply.ReplyInputDto;
import someone.alcoholic.service.reply.ReplyService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ReplyController {
    private final ReplyService replyService;

    @Operation(summary = "댓글 조회", description = "특정 게시물의 댓글들을 조회한다.")
    @GetMapping("board/{boardSeq}/replies")
    public ResponseEntity<ApiResult<Page<ReplyDto>>> getReplies(@PathVariable long boardSeq, Pageable pageable) {
        Page<ReplyDto> replies = replyService.getReplies(pageable, boardSeq);
        return ApiProvider.success(replies);
    }

    @Operation(summary = "댓글 생성", description = "댓글을 생성한다.")
    @PostMapping("board/{boardSeq}/reply")
    public ResponseEntity<ApiResult<ReplyDto>> addReply(@PathVariable long boardSeq, @Valid @RequestBody ReplyInputDto replyInputDto, Principal principal) {
        ReplyDto replyDto = replyService.addReply(replyInputDto, boardSeq, principal.getName());
        return ApiProvider.success(replyDto);
    }

    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정한다.")
    @PutMapping("reply/{replySeq}")
    public ResponseEntity<ApiResult<ReplyDto>> modifyReply(@PathVariable long replySeq, @Valid @RequestBody ReplyInputDto replyInputDto, Principal principal) {
        ReplyDto replyDto = replyService.modifyReply(replyInputDto, principal.getName(), replySeq);
        return ApiProvider.success(replyDto);
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제한다.")
    @DeleteMapping("reply/{replySeq}")
    public ResponseEntity<ApiResult> deleteReply(@PathVariable long replySeq) {
        replyService.deleteReply(replySeq);
        return ApiProvider.success();
    }
}
