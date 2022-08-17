package someone.alcoholic.service.category;

import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.dto.board.BoardCategoryDto;

import java.util.List;

public interface BoardCategoryService {

    List<BoardCategoryDto> getBoardCategories();
    BoardCategoryDto getBoardCategory(long categorySeq);

}
