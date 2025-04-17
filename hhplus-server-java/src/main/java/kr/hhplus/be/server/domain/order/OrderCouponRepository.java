package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderCoupon;

public interface OrderCouponRepository {

    OrderCoupon getByOrderId(Long orderId);

    void save(OrderCoupon orderCoupon);

}
