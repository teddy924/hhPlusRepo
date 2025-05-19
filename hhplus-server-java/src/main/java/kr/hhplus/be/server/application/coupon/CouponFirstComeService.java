package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Slf4j
@Service
public class CouponFirstComeService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CouponStockScheduler couponStockScheduler;

    public CouponFirstComeService(CouponRepository couponRepository,
                                  CouponIssueRepository couponIssueRepository,
                                  @Qualifier("masterRedisTemplate") RedisTemplate<String, Object> redisTemplate, CouponStockScheduler couponStockScheduler) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.redisTemplate = redisTemplate;
        this.couponStockScheduler = couponStockScheduler;
    }

    @Transactional
    public void issueCouponFirstCome(CouponIssueCommand command) {
        Long userId = command.userId();
        Long couponId = command.couponId();

        String stockKey = CacheKey.stock(couponId);             // 정렬을 위해 zset 사용
        String issuedSetKey = CacheKey.issuedSet(couponId);     // 발급여부는 순서와 무관하므로 set 사용
        log.info("stockKey:{}", stockKey);
        log.info("issuedSetKey:{}", issuedSetKey);

        // 중복 발급 여부 체크
        Boolean alreadyIssued = redisTemplate.opsForSet().isMember(issuedSetKey, userId);       // 발급이력 set에 userId가 있는지 체크
        if (Boolean.TRUE.equals(alreadyIssued)) {
            throw new CustomException(DUPLICATE_ISSUE_COUPON);
        }

        // 발급 시도 (선착순 삽입)
        long added = Boolean.TRUE.equals(redisTemplate.opsForZSet().add(stockKey, userId.toString(), System.currentTimeMillis())) ? 1L : 0L;        // 발급순서 zset에 유저를 현재 시각으로 add
        if (added == 0L) {      // zset add에 실패하면 이미 해당 유저는 발급 시도 중
            throw new CustomException(DUPLICATE_TRY_ISSUE);
        }

        // 발급 인원 초과 시 제거
        Coupon coupon = couponRepository.getById(couponId);
        Long rank = redisTemplate.opsForZSet().rank(stockKey, userId.toString());

        if (rank == null || rank >= coupon.getLimitQuantity()) {
            redisTemplate.opsForZSet().remove(stockKey, userId.toString());
            throw new CustomException(COUPON_SOLD_OUT);
        }

        // DB 기준 실제 재고 검증
        if (coupon.getRemainQuantity() <= 0) {
            redisTemplate.opsForZSet().remove(stockKey, userId.toString());
            throw new CustomException(COUPON_SOLD_OUT);
        }

        // 쿠폰 수량 감소 (DB) - scheduler
//        couponStockScheduler.syncCouponStockToDB();
        // @todo 스케쥴러 강제화 제거
        // 스케쥴러로 인해 자동으로 돌아가고 있기 때문에 로직에서는 제거 필요
        // 로직에서 선언할 경우 계획과 다르게 강제로 실행 됨.

        // 발급 이력 저장 (DB) - async
        saveCouponIssueAsnyc(command);

        // 발급 성공 기록
//        redisTemplate.opsForSet().add(issuedSetKey, userId.toString());     // 발급이력 set에 유저 add
        // 🌟 refactoring - TTL 설정 - 쿠폰 유효성 검증
        couponCacheIssuedUser(issuedSetKey, userId, coupon.getEfctFnsDt());

    }

    @Async
    public void saveCouponIssueAsnyc(CouponIssueCommand command) {
        CouponIssue issue = CouponIssue.create(command.toInfo());
        couponIssueRepository.save(issue);
    }

    public void couponCacheIssuedUser(String issuedSetKey, Long userId, LocalDateTime efctFnsDt) {
        redisTemplate.opsForSet().add(issuedSetKey, userId.toString());     // 발급이력 set에 유저 add

        Duration ttl = Duration.between(LocalDateTime.now(), efctFnsDt);    // 현재 시각과 쿠폰종료시각의 차이를 계산
        if (!ttl.isNegative()) {                                            // 음수 : 이미 쿠폰 만료 | 양수 : ttl이 남은 기간
            redisTemplate.expire(issuedSetKey, ttl);                        // 남은 기간 뒤에 자동으로 cache 삭제
        }
    }
}
