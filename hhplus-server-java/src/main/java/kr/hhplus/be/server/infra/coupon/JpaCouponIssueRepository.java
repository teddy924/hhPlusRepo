package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCouponIssueRepository extends JpaRepository<CouponIssue, Long> {

    List<CouponIssue> findAllByUserId(Long userId);

    Optional<CouponIssue> findByUserIdAndCouponId(Long userId, Long couponId);

    Optional<CouponIssue> findByIdAndUserId(Long couponIssueId, Long userId);

    Optional<Long> countByCouponId(Long couponId);
}
