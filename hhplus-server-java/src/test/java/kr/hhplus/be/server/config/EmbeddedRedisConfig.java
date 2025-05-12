package kr.hhplus.be.server.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import java.io.File;
import java.io.IOException;


@TestConfiguration
public class EmbeddedRedisConfig {

    private RedisServer master;
    private RedisServer slave1;
    private RedisServer slave2;

    @PostConstruct
    public void startRedis() throws IOException {
        File redisBinary = new File("/opt/homebrew/bin/redis-server");

        master = new RedisServer(6379, redisBinary);
        slave1 = new RedisServer(6380, redisBinary);
        slave2 = new RedisServer(6381, redisBinary);

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
