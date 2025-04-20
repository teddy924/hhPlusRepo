package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;

import java.util.List;

public interface CouponIssueRepository {

    List<CouponIssue> getAllByUserId(Long userId);

    void save(CouponIssue couponIssue);

    CouponIssue getByUserIdAndCouponId(Long userId, Long couponId);

    CouponIssue getByIdAndUserId(Long couponIssueId, Long userId);
}
