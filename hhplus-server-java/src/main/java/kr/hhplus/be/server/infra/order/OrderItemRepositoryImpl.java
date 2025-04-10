package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.interfaces.order.OrderItemDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

    @Override
    public List<OrderItem> findItemByOrderId(Long id) {
        return List.of();
    }

    @Override
    public void saveOrderItems(List<OrderItem> orderItems) {

    }

    @Override
    public List<OrderItemDTO> findAllByOrderId(Long id) {
        return List.of();
    }
}
