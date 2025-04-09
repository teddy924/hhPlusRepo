package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> findById(Long couponId);

    List<Coupon> findByCouponIds(List<Long> couponIds);

    void save(Coupon coupon);

}
