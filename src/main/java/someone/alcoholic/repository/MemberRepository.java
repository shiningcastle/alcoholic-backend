package someone.alcoholic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}
