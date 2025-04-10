package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderHistoryRepository;
import kr.hhplus.be.server.domain.order.entity.OrderHistory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderHistoryRepositoryImpl implements OrderHistoryRepository {

    @Override
    public Optional<OrderHistory> findHistoryById(Long id) {
        return Optional.empty();
    }

    @Override
    public void saveOrderHistory(OrderHistory orderHistory) {

    }
}
