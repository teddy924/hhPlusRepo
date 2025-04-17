package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

import java.util.List;

public interface CouponRepository {

    Coupon getById(Long couponId);

    List<Coupon> getByCouponIds(List<Long> couponIds);

    void save(Coupon coupon);

}
