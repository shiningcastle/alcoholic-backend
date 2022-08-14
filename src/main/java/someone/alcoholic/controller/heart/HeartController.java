package someone.alcoholic.controller.heart;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.heart.HeartService;

import javax.validation.constraints.Positive;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/heart")
public class HeartController {

    private final HeartService heartService;

    @Operation(summary = "게시물 좋아요 등록 (인증 필요)", description = "유저가 게시물의 좋아요를 등록")
    @Secured("ROLE_USER")
    @PostMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> saveBoardHeart(@PathVariable @Positive @ApiParam(value = "글번호", required = true) Long boardSeq, Principal principal) {
        heartService.saveBoardHeart(principal, boardSeq);
        return ApiProvider.success(MessageEnum.HEART_SAVE_SUCCESS);
    }

    @Operation(summary = "게시물 좋아요 삭제 (인증 필요)", description = "유저가 게시물의 좋아요를 삭제")
    @Secured("ROLE_USER")
    @DeleteMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> deleteBoardHeart(@PathVariable @Positive @ApiParam(value = "글번호", required = true) Long boardSeq, Principal principal) {
        heartService.deleteBoardHeart(principal, boardSeq);
        return ApiProvider.success(MessageEnum.HEART_DELETE_SUCCESS);
    }

}
