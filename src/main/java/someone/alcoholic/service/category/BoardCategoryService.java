package someone.alcoholic.service.category;

import someone.alcoholic.domain.category.BoardCategory;

import java.util.List;

public interface BoardCategoryService {

    List<BoardCategory> getBoardCategories();
    BoardCategory getBoardCategory(long categorySeq);

}
