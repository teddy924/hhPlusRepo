package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.common.CacheKey;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

import static kr.hhplus.be.server.domain.product.ProductRankingPolicy.TOP_COUNT;

@Service
@RequiredArgsConstructor
public class RankingRedisSortedSetService {

    private final RedissonClient redissonClient;

    public void increaseSales(String category, Long productId, int quantity) {
        String key = CacheKey.category(category);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.addScore(productId, quantity);
    }

    public void decreaseSales(String category, Long productId, int quantity) {
        String key = CacheKey.category(category);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.addScore(productId, -quantity);
    }

    public Set<ScoredEntry<Long>> getRank(String category) {
        String key = CacheKey.category(category);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet(key);
        return new LinkedHashSet<>(sortedSet.entryRangeReversed(0, TOP_COUNT * 2 - 1));
    }
}
