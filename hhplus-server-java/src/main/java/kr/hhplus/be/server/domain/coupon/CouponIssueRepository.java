package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;

import java.util.List;
import java.util.Optional;

public interface CouponIssueRepository {

    List<CouponIssue> findAllByUserId(Long userId);

    void save(CouponIssue couponIssue);

    Optional<CouponIssue> findByUserIdAndCouponId(Long userId, Long couponId);

    Optional<CouponIssue> findByIdAndUserId(Long couponIssueId, Long userId);
}
