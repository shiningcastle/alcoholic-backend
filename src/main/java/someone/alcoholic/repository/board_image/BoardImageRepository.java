package someone.alcoholic.repository.board_image;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.board_image.BoardImage;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {

    List<BoardImage> findBySeqIn(List<Long> seqList);
}
