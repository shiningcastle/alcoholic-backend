package someone.alcoholic.service.category;

import someone.alcoholic.domain.category.BoardCategory;

import java.util.List;

public interface BoardCategoryService {
    BoardCategory getBoardCategory(String boardCategoryName);
    List<BoardCategory> getBoardCategories();
}
