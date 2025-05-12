package kr.hhplus.be.server.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> masterRedisTemplate() {
        return buildTemplate("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, String> slave1RedisTemplate() {
        return buildTemplate("localhost", 6380);
    }

    @Bean
    public RedisTemplate<String, String> slave2RedisTemplate() {
        return buildTemplate("localhost", 6381);
    }

    private RedisTemplate<String, String> buildTemplate(String host, int port) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

}
