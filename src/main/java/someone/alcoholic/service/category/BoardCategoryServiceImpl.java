package someone.alcoholic.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.category.BoardCategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardCategoryServiceImpl implements BoardCategoryService{
    private final BoardCategoryRepository boardCategoryRepository;

    public BoardCategory getBoardCategory(String boardCategoryName) {
        return boardCategoryRepository.findByName(boardCategoryName)
                .orElseThrow(() -> new CustomRuntimeException(ExceptionEnum.CATEGORY_NOT_FOUND));
    }
}