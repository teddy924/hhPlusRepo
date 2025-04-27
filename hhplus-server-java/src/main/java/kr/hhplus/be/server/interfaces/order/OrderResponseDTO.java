package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "주문 결제 응답 DTO")
public record OrderResponseDTO(
        Long orderId,
        Long totalAmount,
        OrderStatus orderStatus,
        LocalDateTime orderedAt
) {
    public static OrderResponseDTO from(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getSysCretDt()
        );
    }
}