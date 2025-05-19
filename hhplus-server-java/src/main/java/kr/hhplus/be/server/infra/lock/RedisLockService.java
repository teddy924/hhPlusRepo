package kr.hhplus.be.server.infra.lock;

import kr.hhplus.be.server.common.LockService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Profile("test")
@Service
public class RedisLockService implements LockService {

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
            "else " +
                "return 0 " +
            "end";

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisLockService(@Qualifier("masterRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param key 락 키
     * @param timeout TTL (ex. Duration.ofSeconds(3))
     * @return UUID 값 (락을 획득하지 못하면 null)
     */
    @Override
    public String tryLock(String key, Duration timeout) {
        String value = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, timeout);
        return Boolean.TRUE.equals(success) ? value : null;
    }

    /**
     * @param key 락 키
     * @param value tryLock()에서 반환된 UUID 값
     */
    @Override
    public void unlock(String key, String value) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(UNLOCK_SCRIPT);
        script.setResultType(Long.class);
        redisTemplate.execute(script, Collections.singletonList(key), value);
    }
}
