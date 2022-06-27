package someone.alcoholic.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(String id);
    Optional<Member> findByNickname(String nickname);
}
