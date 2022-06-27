package someone.alcoholic.repository.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import someone.alcoholic.domain.Board.Board;
import someone.alcoholic.domain.category.BoardCategory;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByBoardCategory(BoardCategory boardCategory, Pageable pageable);
}
