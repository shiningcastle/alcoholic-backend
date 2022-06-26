package someone.alcoholic.controller.reply;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.ReplyDto;
import someone.alcoholic.dto.ReplyInputDto;
import someone.alcoholic.service.reply.ReplyService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("board/{boardSeq}/repllies")
    public ApiResult<Page<ReplyDto>> getReplies(@PathVariable int boardSeq, Pageable pageable) {
        Page<ReplyDto> replies = replyService.getReplies(pageable, boardSeq);
        return ApiProvider.success(replies);
    }

    @PostMapping("board/{boardSeq}/reply")
    public ApiResult<ReplyDto> addReply(@PathVariable int boardSeq, @RequestBody ReplyInputDto replyInputDto, Principal principal) {
        ReplyDto replyDto = replyService.addReply(replyInputDto, boardSeq, principal.getName());
        return ApiProvider.success(replyDto);
    }
}
