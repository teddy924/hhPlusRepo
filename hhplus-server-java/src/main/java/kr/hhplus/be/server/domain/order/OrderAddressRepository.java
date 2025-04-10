package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderAddress;
import kr.hhplus.be.server.interfaces.order.OrderAddressDTO;

public interface OrderAddressRepository {

    OrderAddress findAddressById(Long id);

    void saveOrderAddress(OrderAddress orderAddress);

    OrderAddressDTO findByOrderId(Long orderId);
}
