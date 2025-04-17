package kr.hhplus.be.server.domain.coupon;

import lombok.Builder;

import java.util.ArrayList;

@Builder
public record CouponIssueCommand(
        Long userId,
        Long couponId
) {
    public CouponIssueInfo toInfo(){
        return new CouponIssueInfo(userId, couponId, new ArrayList<>());
    }
}
