package someone.alcoholic.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.dto.board.BoardCategoryDto;
import someone.alcoholic.service.category.BoardCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardCategoryController {
    private final BoardCategoryService boardCategoryService;

    @GetMapping("/board-categories")
    public ResponseEntity<ApiResult<List<BoardCategoryDto>>> getBoardCategories() {
        return ApiProvider.success(boardCategoryService.getBoardCategories());
    }
}
