package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

import java.util.List;

public interface CouponRepository {

    List<Coupon> findByCouponIds(List<Long> couponIds);

}
