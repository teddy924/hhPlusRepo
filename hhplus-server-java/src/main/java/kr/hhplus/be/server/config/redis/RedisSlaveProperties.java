package kr.hhplus.be.server.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "slaves")
public class RedisSlaveProperties {

    private List<RedisHostPort> redis;

    @Getter
    @Setter
    public static class RedisHostPort {
        private String host;
        private int port;
    }
}

