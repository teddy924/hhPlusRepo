package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderAddressRepository;
import kr.hhplus.be.server.domain.order.entity.OrderAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class OrderAddressRepositoryImpl implements OrderAddressRepository {

    private final JpaOrderAddressRepository jpaOrderAddressRepository;

    @Override
    public OrderAddress getByOrderId(Long orderId) {
        return jpaOrderAddressRepository.findByOrderId(orderId).orElseThrow(() -> new CustomException(NOT_EXIST_ORDER_ADDRESS));
    }

    @Override
    public void save(OrderAddress orderAddress) {
        jpaOrderAddressRepository.save(orderAddress);
    }

}
