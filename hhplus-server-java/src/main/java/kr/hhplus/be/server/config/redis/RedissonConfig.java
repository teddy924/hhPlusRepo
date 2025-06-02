package kr.hhplus.be.server.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(RedisSlaveProperties.class)
@Profile("!test")
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String masterHost;

    @Value("${spring.data.redis.port}")
    private int masterPort;

    private final RedisSlaveProperties redisSlaveProperties;

    public RedissonConfig(RedisSlaveProperties redisSlaveProperties) {
        this.redisSlaveProperties = redisSlaveProperties;
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 마스터 설정
        String masterAddress = "redis://" + masterHost + ":" + masterPort;
        config.useMasterSlaveServers()
                .setMasterAddress(masterAddress)
                .setReadMode(ReadMode.SLAVE)
                .setSubscriptionMode(SubscriptionMode.SLAVE)
        ;

        // 슬레이브 주소 추가
        for (RedisSlaveProperties.RedisHostPort slave : redisSlaveProperties.getRedis()) {
            String address = "redis://" + slave.getHost() + ":" + slave.getPort();
            config.useMasterSlaveServers().addSlaveAddress(address);
        }

        return Redisson.create(config);
    }
}