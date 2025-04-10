package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CouponIssueResult(
        Long couponId,
        CouponStatus status,
        LocalDateTime issuedDt,
        LocalDateTime usedDt
) {
}
