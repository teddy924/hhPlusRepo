package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.entity.OrderCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderCouponRepository extends JpaRepository<OrderCoupon, Long> {

    OrderCoupon findByOrderId(Long orderId);

}
