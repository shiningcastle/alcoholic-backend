package someone.alcoholic.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.dto.board.BoardCategoryDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.category.BoardCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardCategoryServiceImpl implements BoardCategoryService{
    private final BoardCategoryRepository boardCategoryRepository;

    public BoardCategoryDto getBoardCategory(long categorySeq) {
        log.info("{}번 카테고리 조회", categorySeq);
        BoardCategory boardCategory = boardCategoryRepository.findById(categorySeq)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.CATEGORY_NOT_FOUND));
        return boardCategory.convertBoardDto();
    }

    @Override
    public List<BoardCategoryDto> getBoardCategories() {
        log.info("boardCategories {} 조회");
        List<BoardCategory> boardCategoryRepositoryAll = boardCategoryRepository.findAll();
        return boardCategoryRepositoryAll.stream().map(BoardCategory::convertBoardDto).collect(Collectors.toList());
    }
}
