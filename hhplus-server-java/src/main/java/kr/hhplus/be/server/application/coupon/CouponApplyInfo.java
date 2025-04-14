package kr.hhplus.be.server.application.coupon;

import lombok.Builder;

@Builder
public record CouponApplyInfo(
        Long discountAmount,
        Long finalPayableAmount
) {
}
