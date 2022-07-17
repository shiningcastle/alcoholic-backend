package someone.alcoholic.repository.heart;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.heart.Heart;
import someone.alcoholic.domain.member.Member;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    boolean existsByMemberAndBoard(Member member, Board board);
}
