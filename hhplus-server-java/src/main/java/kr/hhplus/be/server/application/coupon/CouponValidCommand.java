package kr.hhplus.be.server.application.coupon;

import lombok.Builder;

@Builder
public record CouponValidCommand (
        Long userId,
        Long couponId
) {
}
