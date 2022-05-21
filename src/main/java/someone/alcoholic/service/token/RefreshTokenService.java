package someone.alcoholic.service.token;

import someone.alcoholic.domain.token.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByIdAndMemberId(UUID id, String memberId);
}
