package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderCouponRepository;
import kr.hhplus.be.server.domain.order.entity.OrderCoupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderCouponRepositoryImpl implements OrderCouponRepository {

    @Override
    public Optional<OrderCoupon> findCouponById(Long id) {
        return Optional.empty();
    }

    @Override
    public void saveOrderCoupon(OrderCoupon orderCoupon) {

    }
}
