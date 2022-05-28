package someone.alcoholic.service.token;

import someone.alcoholic.domain.token.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    void save(UUID uuid,  RefreshToken refreshToken);
    RefreshToken findById(UUID uuid);
    void delete(UUID uuid);
}
