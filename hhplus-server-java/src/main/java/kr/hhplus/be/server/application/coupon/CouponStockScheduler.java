package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Slf4j
@Component
public class CouponStockScheduler {

    private final RedissonClient redissonClient;
    private final CouponRepository couponRepository;

    public CouponStockScheduler(RedissonClient redissonClient,
                                CouponRepository couponRepository
    ) {
        this.redissonClient = redissonClient;
        this.couponRepository = couponRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void syncCouponStockToDB() {
        // 1. 활성 쿠폰 ID 리스트 조회
        RSet<String> activeCouponIdSet = redissonClient.getSet(CacheKey.activeCouponIds());
        Set<String> idStrings = activeCouponIdSet.readAll();

        if (idStrings.isEmpty()) {
            log.info("[CouponScheduler] 동기화 대상 쿠폰 없음.");
            return;
        }

        // 2. 쿠폰 ID 문자열 → Long 변환
        List<Long> couponIds = idStrings.stream()
                .map(Long::valueOf)
                .toList();

        // 3. DB에서 쿠폰 엔티티 조회
        List<Coupon> couponList = couponRepository.getByCouponIds(couponIds);

        int updated = 0;
        for (Coupon coupon : couponList) {
            String stockKey = CacheKey.stock(coupon.getId());
            RScoredSortedSet<String> stockSet = redissonClient.getScoredSortedSet(stockKey);

            int issuedCount = stockSet.size();
            int remain = coupon.getLimitQuantity() - issuedCount;

            coupon.updateRemainQuantityFromRedis(remain);
            couponRepository.save(coupon);
            updated++;
        }

        log.info("[CouponScheduler] {}개 쿠폰 재고 DB 동기화 완료", updated);
    }

}
