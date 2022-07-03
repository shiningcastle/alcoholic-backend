package someone.alcoholic.controller.reply;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.ReplyDto;
import someone.alcoholic.dto.ReplyInputDto;
import someone.alcoholic.service.reply.ReplyService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("board/{boardSeq}/replies")
    public ResponseEntity<ApiResult<Page<ReplyDto>>> getReplies(@PathVariable long boardSeq, Pageable pageable) {
        Page<ReplyDto> replies = replyService.getReplies(pageable, boardSeq);
        return ApiProvider.success(replies);
    }

    @PostMapping("board/{boardSeq}/reply")
    public ResponseEntity<ApiResult<ReplyDto>> addReply(@PathVariable long boardSeq, @Valid @RequestBody ReplyInputDto replyInputDto, Principal principal) {
        ReplyDto replyDto = replyService.addReply(replyInputDto, boardSeq, principal.getName());
        return ApiProvider.success(replyDto);
    }

    @PutMapping("reply/{replySeq}")
    public ResponseEntity<ApiResult<ReplyDto>> modifyReply(@PathVariable long replySeq, @Valid @RequestBody ReplyInputDto replyInputDto, Principal principal) {
        ReplyDto replyDto = replyService.modifyReply(replyInputDto, principal.getName(), replySeq);
        return ApiProvider.success(replyDto);
    }

    @DeleteMapping("reply/{replySeq}")
    public ResponseEntity<ApiResult> deleteReply(@PathVariable long replySeq) {
        replyService.deleteReply(replySeq);
        return ApiProvider.success();
    }
}
