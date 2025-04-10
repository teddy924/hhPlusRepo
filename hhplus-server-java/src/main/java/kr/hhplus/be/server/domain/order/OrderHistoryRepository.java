package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderHistory;

import java.util.Optional;

public interface OrderHistoryRepository {

    Optional<OrderHistory> findHistoryById(Long id);

    void saveOrderHistory(OrderHistory orderHistory);

}
