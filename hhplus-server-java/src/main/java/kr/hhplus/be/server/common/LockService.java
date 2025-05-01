package kr.hhplus.be.server.common;

import java.time.Duration;

public interface LockService {

    boolean tryLock(String key, String value, Duration timeout);
    boolean unlock(String key, String value);

}
