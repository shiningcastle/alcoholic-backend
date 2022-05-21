package someone.alcoholic.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.repository.token.RefreshTokenRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByIdAndMemberId(UUID id, String memberId) {
        return refreshTokenRepository.findByIdAndMemberId(id, memberId);
    }
}
