package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponDiscountType;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;

import java.time.LocalDateTime;

public record CouponQueryDto (
        Long couponId
        , String couponName
        , CouponDiscountType discountType
        , Long discountValue
        , CouponStatus status
        , LocalDateTime issuedDt
        , LocalDateTime usedDt
) {
    public static CouponQueryDto from(Coupon coupon, CouponIssue couponIssue) {
        return new CouponQueryDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                couponIssue.getStatus(),
                couponIssue.getIssuedDt(),
                couponIssue.getUsedDt()
        );
    }
}
