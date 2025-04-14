package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.Builder;

@Builder
public record CouponInfo(
        Coupon coupon,
        CouponIssue couponIssue
) {
    public CouponApplyInfo applyDiscount(long totalPrice) {
        long discountAmount = coupon.calculateCoupon(totalPrice);
        long finalAmount = Math.max(0L, totalPrice - discountAmount);
        return CouponApplyInfo.builder()
                .discountAmount(discountAmount)
                .finalPayableAmount(finalAmount)
                .build();
    }
}
