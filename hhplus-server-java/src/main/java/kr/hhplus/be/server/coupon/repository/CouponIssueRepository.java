package kr.hhplus.be.server.coupon.repository;

import kr.hhplus.be.server.coupon.entity.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
}
