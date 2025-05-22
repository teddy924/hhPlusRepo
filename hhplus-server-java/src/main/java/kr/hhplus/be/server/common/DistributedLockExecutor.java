package kr.hhplus.be.server.common;

import kr.hhplus.be.server.common.exception.LockAcquireFailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DistributedLockExecutor {

    private final LockService lockService;

    public DistributedLockExecutor(@Qualifier("redissonLockService")LockService lockService) {
        this.lockService = lockService;
    }

    public void runWithLock(String key, Runnable task) {
        String lockValue = lockService.tryLock(key, Duration.ofMillis(300));
        if (lockValue == null) throw new LockAcquireFailException();

        try {
            task.run();
        } finally {
            lockService.unlock(key, lockValue);
        }
    }
}
