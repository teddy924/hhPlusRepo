package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.interfaces.order.OrderItemDTO;

import java.util.List;

public interface OrderItemRepository {

    List<OrderItem> getByOrderId(Long orderId);

    void save(List<OrderItem> orderItems);

    List<OrderItemDTO> getDTOByOrderId(Long orderId);

    List<OrderItem> getByOrderIds(List<Long> orderIds);
}
