package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.CacheKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

import static kr.hhplus.be.server.domain.product.ProductRankingPolicy.TOP_COUNT;

@Service
public class RankingRedisSortedSetService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RankingRedisSortedSetService(@Qualifier("masterRedisTemplate")RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void increaseSales(String category, Long productId, int quantity) {
        String key = CacheKey.category(category);
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), quantity);
    }

    public void decreaseSales(String category, Long productId, int quantity) {
        String key = CacheKey.category(category);
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), -quantity);
    }

    public Set<ZSetOperations.TypedTuple<Object>> getRank(String category) {
        String key = CacheKey.category(category);
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, (TOP_COUNT * 2) - 1);
    }

}
