package kr.hhplus.be.server.infra.lock;

import kr.hhplus.be.server.common.LockService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisLockService implements LockService {

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
            "else " +
                "return 0 " +
            "end";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisLockService(@Qualifier("masterRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(String key, String value, Duration expireTime) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, value, expireTime)
        );
    }

    @Override
    public boolean unlock(String key, String value) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(UNLOCK_SCRIPT);
        script.setResultType(Long.class);
        Long result = redisTemplate.execute(script, List.of(key), value);
        return result == 1;
    }
}
