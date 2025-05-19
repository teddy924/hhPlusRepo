package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Slf4j
@Component
public class CouponStockScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CouponRepository couponRepository;

    public CouponStockScheduler(@Qualifier("masterRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                CouponRepository couponRepository
    ) {
        this.redisTemplate = redisTemplate;
        this.couponRepository = couponRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void syncCouponStockToDB() {
        Set<String> keys = redisTemplate.keys(CacheKey.getCouponStockZsetKeyPattern());       //  redis에서 쿠폰 재고 키 패턴을 통해 키 추출

        if (keys.isEmpty()) {
            return;
        }
        List<Long> couponIds = keys.stream()
                .map(key -> key.replace(CacheKey.getCouponStockZsetKeyPrefix() + ":", ""))      // 쿠폰id 추출을 위해 key prefix 제거
                .map(Long::valueOf)
                .toList();

        List<Coupon> couponList = couponRepository.getByCouponIds(couponIds);

        for (Coupon coupon : couponList) {
            String stockKey = CacheKey.stock(coupon.getId());

            Long issuedCount = redisTemplate.opsForZSet().zCard(stockKey); // 발급 수
            if (issuedCount == null) issuedCount = 0L;

            int remain = coupon.getLimitQuantity() - issuedCount.intValue();
            coupon.updateRemainQuantityFromRedis(remain);

            couponRepository.save(coupon);
        }

    }

}
