package someone.alcoholic.repository.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import someone.alcoholic.domain.token.RefreshToken;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    RefreshToken findByIdAndMemberId(UUID id, String memberId);
}

