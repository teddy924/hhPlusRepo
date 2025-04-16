package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderAddress;
import kr.hhplus.be.server.interfaces.order.OrderAddressDTO;

public interface OrderAddressRepository {

    OrderAddress getByOrderId(Long orderId);

    void save(OrderAddress orderAddress);

    OrderAddressDTO getDTOByOrderId(Long orderId);
}
