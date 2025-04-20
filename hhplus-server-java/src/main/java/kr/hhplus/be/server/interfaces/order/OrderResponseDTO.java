package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "주문 결제 Response DTO")
public record OrderResponseDTO(
        @Schema(description = "주문 ID") Long orderId,
        @Schema(description = "주문 금액") Long totalAmount,
        @Schema(description = "주문 상태") OrderStatus orderStatus,
        @Schema(description = "주문 일자") LocalDateTime orderedDt
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