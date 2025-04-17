package kr.hhplus.be.server.domain.coupon;

import lombok.Builder;

@Builder
public record CouponApplyInfo(
        Long discountAmount,
        Long finalPayableAmount
) {
}
