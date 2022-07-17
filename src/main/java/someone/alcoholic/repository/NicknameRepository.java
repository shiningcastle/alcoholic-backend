package someone.alcoholic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import someone.alcoholic.domain.nickname.Nickname;

@Repository
public interface NicknameRepository extends JpaRepository<Nickname, Long> {
}
