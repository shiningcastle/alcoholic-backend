package someone.alcoholic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
}
