package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderCouponRepository;
import kr.hhplus.be.server.domain.order.entity.OrderCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class OrderCouponRepositoryImpl implements OrderCouponRepository {

    private final JpaOrderCouponRepository jpaOrderCouponRepository;

    @Override
    public OrderCoupon getByOrderId(Long orderId) {
        return jpaOrderCouponRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_COUPON));
    }

    @Override
    public void save(OrderCoupon orderCoupon) {
        jpaOrderCouponRepository.save(orderCoupon);
    }
}
