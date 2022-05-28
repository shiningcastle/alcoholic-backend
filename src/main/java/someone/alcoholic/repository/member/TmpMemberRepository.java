package someone.alcoholic.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import someone.alcoholic.domain.member.TmpMember;

import java.util.Optional;

public interface TmpMemberRepository extends JpaRepository<TmpMember, Integer>{
    Optional<TmpMember> findById(String id);
}
