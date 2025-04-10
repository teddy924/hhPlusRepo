package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderAddressRepository;
import kr.hhplus.be.server.domain.order.entity.OrderAddress;
import kr.hhplus.be.server.interfaces.order.OrderAddressDTO;
import org.springframework.stereotype.Repository;

@Repository
public class OrderAddressRepositoryImpl implements OrderAddressRepository {

    @Override
    public OrderAddress findAddressById(Long id) {
        return null;
    }

    @Override
    public void saveOrderAddress(OrderAddress orderAddress) {

    }

    @Override
    public OrderAddressDTO findByOrderId(Long orderId) {
        return null;
    }
}
