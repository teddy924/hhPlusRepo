package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class CouponCacheManager {

    private final RedissonClient redissonClient;

    public void checkDuplicateIssue(String issuedSetKey, Long userId) {
        RSet<String> issuedSet = redissonClient.getSet(issuedSetKey);
        if (issuedSet.contains(userId.toString())) {
            throw new CustomException(DUPLICATE_ISSUE_COUPON);
        }
    }

    public void tryAddToStock(String stockKey, Long userId) {
        RScoredSortedSet<String> stockSet = redissonClient.getScoredSortedSet(stockKey);
        boolean added = stockSet.add(System.currentTimeMillis(), userId.toString());
        if (!added) {
            throw new CustomException(DUPLICATE_TRY_ISSUE);
        }
    }

    public void checkStockOverflow(String stockKey, Long userId, int limitQuantity) {
        RScoredSortedSet<String> stockSet = redissonClient.getScoredSortedSet(stockKey);
        Long rank = (long) stockSet.rank(userId.toString());
        if (rank == null || rank < 0 || rank >= limitQuantity) {
            stockSet.remove(userId.toString());
            throw new CustomException(COUPON_SOLD_OUT);
        }
    }

    public void checkStockRemain(String stockKey, Long userId, int remainQuantity) {
        RScoredSortedSet<String> stockSet = redissonClient.getScoredSortedSet(stockKey);
        if (remainQuantity <= 0) {
            stockSet.remove(userId.toString());
            throw new CustomException(COUPON_SOLD_OUT);
        }
    }

    public void recordIssueSuccess(String issuedSetKey, Long userId, LocalDateTime efctFnsDt) {
        RSet<String> issuedSet = redissonClient.getSet(issuedSetKey);
        issuedSet.add(userId.toString());

        Duration ttl = Duration.between(LocalDateTime.now(), efctFnsDt);
        if (!ttl.isNegative()) {
            issuedSet.expire(ttl);
        }
    }

    public void registerCouponToActiveSet(Long couponId) {
        RSet<String> activeSet = redissonClient.getSet(CacheKey.activeCouponIds());
        activeSet.add(couponId.toString());
    }
}