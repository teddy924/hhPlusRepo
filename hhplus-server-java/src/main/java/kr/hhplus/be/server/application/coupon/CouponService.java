package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.common.CacheKey;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import kr.hhplus.be.server.interfaces.coupon.CouponResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public CouponService(CouponRepository couponRepository,
                         CouponIssueRepository couponIssueRepository,
                         @Qualifier("masterRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.redisTemplate = redisTemplate;
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
        CouponIssueInfo issueInfo = couponIssueCommand.toInfo();

        // 1. 쿠폰 유효성 확인
        Coupon coupon = couponRepository.getById(issueInfo.couponId());
        coupon.expiredCoupon(); // 유효기간 확인

        // 2. 중복 발급 확인 (CouponIssue 내부에서 책임지도록 위임)
        List<CouponIssue> alreadyIssuedList = couponIssueRepository.getAllByUserId(issueInfo.userId());
        issueInfo.addExistingIssues(alreadyIssuedList);

        CouponIssue issue = CouponIssue.create(issueInfo);

        // 3. 재고 차감
        coupon.useOneQuantity();
        couponRepository.save(coupon);

        // 4. 발급 이력 저장
        couponIssueRepository.save(issue);
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

    @Transactional
    public void issueCouponFirstCome(CouponIssueCommand command) {
        Long userId = command.userId();
        Long couponId = command.couponId();

        String stockKey = CacheKey.stock(couponId);             // 정렬을 위해 zset 사용
        String issuedSetKey = CacheKey.issuedSet(couponId);     // 발급여부는 순서와 무관하므로 set 사용

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
        int totalStock = coupon.getLimitQuantity();
        Long rank = redisTemplate.opsForZSet().rank(stockKey, userId.toString());       // 유저가 몇 번째 발급자인지 추출
        if (rank == null || rank >= totalStock) {       // 발급순서가 총 발급개능 개수와 일치하거나 크면 재고 없음
            redisTemplate.opsForZSet().remove(stockKey, userId.toString());     // 재고가 없으면 발급 대기열 zset에서 해당 유저 제거
            throw new CustomException(COUPON_SOLD_OUT);
        }

        // 쿠폰 수량 감소 (DB)
        coupon.useOneQuantity();
        couponRepository.save(coupon);

        // 발급 이력 저장 (DB)
        CouponIssue issue = CouponIssue.create(command.toInfo());
        couponIssueRepository.save(issue);

        // 발급 성공 기록
        redisTemplate.opsForSet().add(issuedSetKey, userId.toString());     // 발급이력 set에 유저 add
    }


}
