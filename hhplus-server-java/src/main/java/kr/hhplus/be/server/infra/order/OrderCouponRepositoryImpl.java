package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderCouponRepository;
import kr.hhplus.be.server.domain.order.entity.OrderCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderCouponRepositoryImpl implements OrderCouponRepository {

    private final JpaOrderCouponRepository jpaOrderCouponRepository;

    @Override
    public OrderCoupon getByOrderId(Long orderId) {
        return jpaOrderCouponRepository.findByOrderId(orderId);
    }

    @Override
    public void save(OrderCoupon orderCoupon) {
        jpaOrderCouponRepository.save(orderCoupon);
    }
}
