package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.interfaces.order.OrderItemDTO;

import java.util.List;

public interface OrderItemRepository {

    List<OrderItem> findItemByOrderId(Long id);

    void saveOrderItems(List<OrderItem> orderItems);

    List<OrderItemDTO> findAllByOrderId(Long id);
}
