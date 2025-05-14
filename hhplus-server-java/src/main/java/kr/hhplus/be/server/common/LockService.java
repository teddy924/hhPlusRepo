package kr.hhplus.be.server.common;

import java.time.Duration;

public interface LockService {

    String tryLock(String key, Duration timeout);
    void unlock(String key, String value);

}
