package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Slf4j
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final RedissonClient redissonClient;
    private final CouponCacheManager couponCacheManager;

    public CouponService(CouponRepository couponRepository,
                         CouponIssueRepository couponIssueRepository,
                         RedissonClient redissonClient,
                         CouponCacheManager couponCacheManager) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.redissonClient = redissonClient;
        this.couponCacheManager = couponCacheManager;
    }

    // 보유 쿠폰 목록 조회
    public List<CouponResponseDTO> retrieveCouponList(Long userId) {
        // 1. 발급 이력 조회
        List<CouponIssue> couponIssueList = couponIssueRepository.getAllByUserId(userId);

        if (couponIssueList.isEmpty()) {
            throw new CustomException(NOT_HAS_COUPON);
        }

        // 2. 발급 이력에서 바로 Coupon 가져와 DTO로 매핑
        return couponIssueList.stream()
                .map(issue -> CouponResponseDTO.from(issue.getCoupon(), issue))
                .toList();
    }


    // 쿠폰 발급
    @Transactional
    public void issueCoupon(CouponIssueCommand couponIssueCommand) {
        Long userId = couponIssueCommand.userId();
        Long couponId = couponIssueCommand.couponId();

        Coupon coupon = couponRepository.getById(couponId);
        coupon.expiredCoupon(); // 유효기간 확인

        // redis key 선언
        String stockKey = CacheKey.stock(couponId);
        String issuedSetKey = CacheKey.issuedSet(couponId);

        log.info("stockKey:{}", stockKey);
        log.info("issuedSetKey:{}", issuedSetKey);

        // 1. Redis 중복 발급 여부 확인
        couponCacheManager.checkDuplicateIssue(issuedSetKey, userId);

        // 2. Redis 선착순 구조에 추가 (선착순 아닌 일반 발급이라도 발급 수 추적용으로 활용)
        couponCacheManager.tryAddToStock(stockKey, userId);

        // 3. 재고 초과 확인
        couponCacheManager.checkStockOverflow(stockKey, userId, coupon.getLimitQuantity());

        // 4. DB 기준 실제 재고 검증
        couponCacheManager.checkStockRemain(stockKey, userId, coupon.getRemainQuantity());

        // 5. DB 재고 감소 및 이력 저장
        coupon.useOneQuantity();
        couponRepository.save(coupon);
        // 쿠폰 발급 성공 시
        couponCacheManager.registerCouponToActiveSet(couponId);

        // 6. 쿠폰 발급 이력 저장
        CouponIssue issue = CouponIssue.create(couponIssueCommand.toInfo());
        couponIssueRepository.save(issue);

        // 7. 발급 이력 Redis에 기록 + TTL 설정
        couponCacheManager.recordIssueSuccess(issuedSetKey, userId, coupon.getEfctFnsDt());
    }

    // 쿠폰 조회(쿠폰 정보, 이력)
    public CouponInfo retrieveCouponInfo (CouponIssueCommand couponIssueCommand) {

        // 1. 쿠폰 발급 이력 확인
        CouponIssue issue = couponIssueRepository.getByUserIdAndCouponId(couponIssueCommand.userId(), couponIssueCommand.couponId());

        // 2. 쿠폰 엔티티 조회
        Coupon coupon = couponRepository.getById(couponIssueCommand.couponId());

        // 3. 쿠폰 유효성 검사 (만료여부 확인)
        coupon.expiredCoupon();

        return CouponInfo.builder()
                .coupon(coupon)
                .couponIssue(issue)
                .build();

    }

    // 쿠폰 사용
    public void useCoupon (CouponInfo couponInfo) {

        try {
            CouponIssue issue = couponInfo.couponIssue();
            issue.markAsUsed();
        } catch (Exception e) {
            throw new CustomException(FAIL_USE_COUPON);
        }

    }

    // 쿠폰 복구
    public void restoreCoupon(Long userId, Long couponIssueId) {

        try {
            CouponIssue issue = couponIssueRepository.getByIdAndUserId(couponIssueId, userId);
            issue.restore(); // domain method → status = ISSUED, usedDt = null
        } catch (Exception e) {
            throw new CustomException(FAIL_RESTORE_COUPON);
        }
    }

}
