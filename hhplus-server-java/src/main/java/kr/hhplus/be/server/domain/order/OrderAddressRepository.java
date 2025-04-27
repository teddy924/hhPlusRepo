package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderAddress;

public interface OrderAddressRepository {

    OrderAddress getByOrderId(Long orderId);

    void save(OrderAddress orderAddress);

}
