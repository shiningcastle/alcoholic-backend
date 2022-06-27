package someone.alcoholic.repository.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.reply.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
//    @Query("select r from Reply r " +
//            "where r.")
    Page<Reply> findAllByBoardOrderByReplyParent(Pageable pageable, Board board);
}
