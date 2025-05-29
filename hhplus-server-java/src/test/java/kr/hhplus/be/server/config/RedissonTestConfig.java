package kr.hhplus.be.server.config;

import kr.hhplus.be.server.config.redis.RedisSlaveProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
@EnableConfigurationProperties(RedisSlaveProperties.class)
public class RedissonTestConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
        ;
        return Redisson.create(config);
    }
}
