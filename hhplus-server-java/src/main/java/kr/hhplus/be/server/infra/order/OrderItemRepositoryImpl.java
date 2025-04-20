package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.interfaces.order.OrderItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final JpaOrderItemRepository jpaOrderItemRepository;

    @Override
    public List<OrderItem> getByOrderId(Long orderId) {
        return jpaOrderItemRepository.findByOrderId(orderId);
    }

    @Override
    public void save(List<OrderItem> orderItems) {
        jpaOrderItemRepository.saveAll(orderItems);
    }

    @Override
    public List<OrderItemDTO> getDTOByOrderId(Long orderId) {
        return jpaOrderItemRepository.findDTOByOrderId(orderId);
    }

    @Override
    public List<OrderItem> getByOrderIds(List<Long> orderIds) {
        return jpaOrderItemRepository.findByOrderIdIn(orderIds);
    }
}
