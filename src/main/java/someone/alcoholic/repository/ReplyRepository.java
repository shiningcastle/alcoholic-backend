package someone.alcoholic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import someone.alcoholic.domain.Board.Board;
import someone.alcoholic.domain.Reply.Reply;
import someone.alcoholic.dto.ReplyDto;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {
//    @Query("select r from Reply r " +
//            "where r.")
    Page<Reply> findAllByBoardOrderByReplyParent(Pageable pageable, Board board);
}
