package kr.hhplus.be.server.coupon.repository;

import kr.hhplus.be.server.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
