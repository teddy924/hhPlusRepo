package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import lombok.Builder;

import java.util.List;

@Builder
public record CouponIssueInfo(
        Long userId,
        Long couponId,
        List<CouponIssue> couponIssueList
) {

}
