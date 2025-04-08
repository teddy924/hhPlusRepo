package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    public void issueCoupon (Long userId, Long couponId) {

        // 1. 쿠폰 유효여부 확인
        Coupon coupon = couponRepository.findById(userId);
        if (coupon == null) {
            // 인입 받은 쿠폰 존재 유무
            throw new CustomException(NOT_EXIST_COUPON);
        }
        else {
            // 쿠폰 유효기간 유효 여부
            coupon.expiredCoupon();
        }

        // 2. 쿠폰 기 발급 여부 확인
        List<CouponIssue> couponIssue = couponIssueRepository.findAllByUserId(userId);

        if (!couponIssue.isEmpty()) {
            couponIssue.forEach(issue -> {
                boolean isDuplicate = issue.getCouponId().equals(couponId);
                if (isDuplicate) {
                    throw new CustomException(DUPLICATE_ISSUE_COUPON);
                }
            });
        }

        // 3. 쿠폰 발급
        coupon.useOneQuantity();
        couponRepository.save(coupon);

        CouponIssue issue = CouponIssue.builder()
                .userId(userId)
                .couponId(couponId)
                .status(CouponStatus.ISSUED)
                .issuedDt(LocalDateTime.now())
                .usedDt(null)
                .build();

        // 4. 쿠폰 발급 이력 저장
        couponIssueRepository.save(issue);

    }

}
