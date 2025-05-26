package kr.hhplus.be.server.infra.lock;

import kr.hhplus.be.server.common.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("redissonLockService")
@RequiredArgsConstructor
public class RedissonLockService implements LockService {

    private final RedissonClient redissonClient;
    private final Map<String, String> lockMap = new ConcurrentHashMap<>();

    /**
     * @param key 락 키
     * @param timeout TTL (ex. Duration.ofSeconds(3))
     * @return UUID 값 (락을 획득하지 못하면 null)
     */
    @Override
    public String tryLock(String key, Duration timeout) {
        String uuid = UUID.randomUUID().toString();
        RLock lock = redissonClient.getLock(key);

        boolean success;
        try {
            success = lock.tryLock(0, timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("Redisson 락 획득 중 인터럽트 발생 key={}", key);
            Thread.currentThread().interrupt();
            return null;
        }

        if (success) {
            lockMap.put(key, uuid);
            return uuid;
        }

        return null;
    }

    /**
     * @param key 락 키
     * @param value tryLock()에서 반환된 UUID 값
     */
    @Override
    public void unlock(String key, String value) {
        RLock lock = redissonClient.getLock(key);

        String storedValue = lockMap.get(key);
        if (storedValue != null && storedValue.equals(value)) {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                lockMap.remove(key);
            }
        }
    }
}
