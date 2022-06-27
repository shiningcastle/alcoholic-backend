package someone.alcoholic.repository.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, Duration duration) {
        log.info("Redis 저장 시작 - {} : {} ({})", key, value, duration);
        redisTemplate.opsForValue().set(key, value, duration);
        log.info("Redis 저장 완료 - {} : {} ({})", key, value, duration);
    }

    public Object get(String key) {
        log.info("Redis 조회 시작 - {}", key);
        Object object = redisTemplate.opsForValue().get(key);
        if (object == null)
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.REDIS_NOT_EXIST);
        log.info("Redis 조회 성공 - {} : {}", key, object);
        return object;
    }

    public void delete(String key) {
        log.info("Redis 삭제 시작 - {}", key);
        Boolean flag = redisTemplate.delete(key);
        if (Boolean.FALSE.equals(flag))
            log.info("Redis 삭제 실패 - {} : Value가 존재하지 않습니다.", key);
        log.info("Redis 삭제 성공 - {}", key);
    }
}
