package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderCoupon;

import java.util.Optional;

public interface OrderCouponRepository {

    Optional<OrderCoupon> findCouponById(Long id);

    void saveOrderCoupon(OrderCoupon orderCoupon);

}
