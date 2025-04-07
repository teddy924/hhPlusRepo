package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

import java.util.List;

public record CouponIssueQueryDto(
        Long userId
        , List<CouponQueryDto> couponIssueList
) {
    public static CouponIssueQueryDto of(Long userId, List<CouponQueryDto> coupons) {
        return new CouponIssueQueryDto(userId, coupons);
    }
}
