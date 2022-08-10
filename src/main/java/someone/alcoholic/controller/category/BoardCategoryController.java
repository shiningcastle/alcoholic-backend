package someone.alcoholic.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.service.category.BoardCategoryService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardCategoryController {
    private final BoardCategoryService boardCategoryService;

    @GetMapping
    public ResponseEntity<ApiResult<List<BoardCategory>>> getBoardCategories() {
        return ApiProvider.success(boardCategoryService.getBoardCategories());
    }
}
