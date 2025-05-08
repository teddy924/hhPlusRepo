package kr.hhplus.be.server.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class RedisSlaveSelector {

    private final List<RedisTemplate<String, Object>> slaveTemplates;
    private final Random random = new Random();

    public RedisSlaveSelector(
            @Qualifier("slave1RedisTemplate") RedisTemplate<String, Object> slave1,
            @Qualifier("slave2RedisTemplate") RedisTemplate<String, Object> slave2
    ) {
        this.slaveTemplates = List.of(slave1, slave2);
    }

    public RedisTemplate<String, Object> getRandomSlave() {
        int index = random.nextInt(slaveTemplates.size());
        return slaveTemplates.get(index);
    }
}
