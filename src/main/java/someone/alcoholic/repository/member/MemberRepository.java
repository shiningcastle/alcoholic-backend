package someone.alcoholic.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findById(String id);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByIdAndEmail(String id, String email);
}
