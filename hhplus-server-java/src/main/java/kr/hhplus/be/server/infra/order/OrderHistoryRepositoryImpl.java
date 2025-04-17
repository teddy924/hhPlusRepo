package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.OrderHistoryRepository;
import kr.hhplus.be.server.domain.order.entity.OrderHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class OrderHistoryRepositoryImpl implements OrderHistoryRepository {

    private final JpaOrderHistoryRepository jpaOrderHistoryRepository;

    @Override
    public OrderHistory getByOrderId(Long orderId) {
        return jpaOrderHistoryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_ORDER));
    }

    @Override
    public void save(OrderHistory orderHistory) {
        jpaOrderHistoryRepository.save(orderHistory);
    }
}
