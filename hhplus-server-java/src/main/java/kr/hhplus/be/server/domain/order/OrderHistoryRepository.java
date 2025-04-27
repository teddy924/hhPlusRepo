package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderHistory;

public interface OrderHistoryRepository {

    OrderHistory getByOrderId(Long id);

    void save(OrderHistory orderHistory);

}
