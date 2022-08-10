package someone.alcoholic.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.category.BoardCategoryRepository;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardCategoryServiceImpl implements BoardCategoryService{
    private final BoardCategoryRepository boardCategoryRepository;

    @Override
    public BoardCategory getBoardCategory(String boardCategoryName) {
        log.info("boardCategory {} 조회", boardCategoryName);
        return boardCategoryRepository.findByName(boardCategoryName)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.CATEGORY_NOT_FOUND));
    }

    @Override
    public List<BoardCategory> getBoardCategories() {
        log.info("boardCategories {} 조회");
        return boardCategoryRepository.findAll();
    }
}
