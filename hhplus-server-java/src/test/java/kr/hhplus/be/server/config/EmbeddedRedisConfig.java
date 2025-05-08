package kr.hhplus.be.server.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;
import redis.embedded.core.RedisServerBuilder;

import java.io.IOException;


@TestConfiguration
public class EmbeddedRedisConfig {

    private RedisServer master;
    private RedisServer slave1;
    private RedisServer slave2;

    @PostConstruct
    public void startRedis() throws IOException {
        master = new RedisServer(6379);

        slave1 = new RedisServerBuilder()
                .port(6380)
                .setting("replicaof 127.0.0.1 6379")
                .build();

        slave2 = new RedisServerBuilder()
                .port(6381)
                .setting("replicaof 127.0.0.1 6379")
                .build();

        master.start();
        slave1.start();
        slave2.start();
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (slave2 != null) slave2.stop();
        if (slave1 != null) slave1.stop();
        if (master != null) master.stop();
    }
}
