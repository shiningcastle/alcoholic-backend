package someone.alcoholic.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.enums.ExpiryTime;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(UUID uuid,  RefreshToken refreshToken) {
        redisTemplate.opsForValue().set(uuid.toString(), refreshToken, Duration.ofHours(ExpiryTime.REFRESH_TOKEN_EXPIRY_HOUR.getTime()));
    }

    @Override
    public RefreshToken findById(UUID uuid) {
        return (RefreshToken) redisTemplate.opsForValue().get(uuid.toString());
    }

    @Override
    public void delete(UUID uuid) {
        redisTemplate.delete(uuid.toString());
    }

}
