package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.interfaces.order.OrderItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    void save(List<OrderItem> orderItems);

    List<OrderItemDTO> findDTOByOrderId(Long orderId);

    List<OrderItem> findByOrderIdIn(List<Long> orderIds);

}
