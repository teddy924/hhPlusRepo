package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;

public record CouponQueryDto (
        Long couponId
        , String couponName
        , CouponDiscountType discountType
        , Long discountValue
) {
    public static CouponQueryDto from(Coupon coupon) {
        return new CouponQueryDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue()
        );
    }
}
