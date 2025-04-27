package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.interfaces.payment.PaymentDTO;

import java.util.List;

public record OrderDetailResponseDTO(
        Long orderId,
        List<OrderItemDTO> items,
        OrderAddressDTO address,
        PaymentDTO payment,
        OrderStatus status
) {
    public static OrderDetailResponseDTO from(Order order, List<OrderItemDTO> items, OrderAddressDTO address, PaymentDTO payment, OrderStatus status) {
        return new OrderDetailResponseDTO(
                order.getId(), items, address, payment, status
        );
    }
}
