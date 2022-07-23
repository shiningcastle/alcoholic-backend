package someone.alcoholic.controller.heart;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.MessageEnum;
import someone.alcoholic.service.heart.HeartService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/heart")
public class HeartController {

    private final HeartService heartService;

    @Operation(summary = "게시물 좋아요 등록", description = "유저가 게시물의 좋아요를 누른다.")
    @PostMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> saveBoardHeart(HttpServletRequest request, @PathVariable @Positive @ApiParam(value = "글번호", required = true) Long boardSeq) {
        heartService.saveBoardHeart(request, boardSeq);
        return ApiProvider.success(MessageEnum.HEART_SAVE_SUCCESS);
    }

    @Operation(summary = "게시물 좋아요 삭제", description = "유저가 게시물의 좋아요를 지운다.")
    @DeleteMapping("/board/{boardSeq}")
    public ResponseEntity<ApiResult> deleteBoardHeart(HttpServletRequest request, @PathVariable @Positive @ApiParam(value = "글번호", required = true) Long boardSeq) {
        heartService.deleteBoardHeart(request, boardSeq);
        return ApiProvider.success(MessageEnum.HEART_DELETE_SUCCESS);
    }
}
