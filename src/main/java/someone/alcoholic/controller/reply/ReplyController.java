package someone.alcoholic.controller.reply;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.dto.reply.ReReplyInputDto;
import someone.alcoholic.dto.reply.ReplyDto;
import someone.alcoholic.dto.reply.ReplyInputDto;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.reply.ReplyService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ReplyController {
    private final ReplyService replyService;

    @Operation(summary = "댓글 조회", description = "특정 게시물의 댓글들을 조회")
    @Secured("ROLE_USER")
    @GetMapping("board/{boardSeq}/replies")
    public ResponseEntity<ApiResult<Page<ReplyDto>>> getReplies(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long boardSeq, Pageable pageable, Principal principal) {
        return ApiProvider.success(replyService.getReplies(pageable, boardSeq, principal.getName()));
    }

    @Operation(summary = "댓글 생성 (인증 필요)", description = "댓글을 생성")
    @Secured("ROLE_USER")
    @PostMapping("board/{boardSeq}/reply")
    public ResponseEntity<ApiResult<ReplyDto>> addReply(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long boardSeq,
                                                        @Valid @RequestBody @ApiParam(value = "댓글 생성 정보", required = true) ReplyInputDto replyInputDto, Principal principal) {
        return ApiProvider.success(replyService.addReply(replyInputDto, boardSeq, principal.getName()), MessageEnum.REPLY_INSERT_SUCCESS);
    }

    @Operation(summary = "대댓글 생성 (인증 필요)", description = "대댓글을 생성")
    @Secured("ROLE_USER")
    @PostMapping("board/{boardSeq}/rereply")
    public ResponseEntity<ApiResult<ReplyDto>> addReReply(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long boardSeq,
                                                          @Valid @RequestBody @ApiParam(value = "댓글 생성 정보", required = true) ReReplyInputDto replyInputDto, Principal principal) {
        return ApiProvider.success(replyService.addReReply(replyInputDto, boardSeq, principal.getName()), MessageEnum.REPLY_INSERT_SUCCESS);
    }


    @Operation(summary = "댓글 수정 (인증 필요)", description = "특정 댓글을 수정")
    @Secured("ROLE_USER")
    @PutMapping("reply/{replySeq}")
    public ResponseEntity<ApiResult<ReplyDto>> modifyReply(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long replySeq,
                                                           @Valid @RequestBody @ApiParam(value = "댓글 생성 정보", required = true) ReplyInputDto replyInputDto, Principal principal) {
        return ApiProvider.success(replyService.modifyReply(replyInputDto, principal.getName(), replySeq), MessageEnum.REPLY_UPDATE_SUCCESS);
    }

    @Operation(summary = "댓글 삭제 (인증 필요)", description = "특정 댓글을 삭제")
    @Secured("ROLE_USER")
    @DeleteMapping("reply/{replySeq}")
    public ResponseEntity<ApiResult> deleteReply(@PathVariable @Positive @ApiParam(value = "글번호", required = true) long replySeq) {
        replyService.deleteReply(replySeq);
        return ApiProvider.success(MessageEnum.REPLY_DELETE_SUCCESS);
    }
}
