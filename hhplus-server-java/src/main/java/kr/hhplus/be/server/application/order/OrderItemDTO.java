package kr.hhplus.be.server.application.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import lombok.Builder;

@Builder
@Schema(description = "주문 상품 정보 DTO")
public record OrderItemDTO(
        Long orderId,
        Long productId,
        Integer quantity,
        Long totalAmount
) {
    public static OrderItemDTO from(OrderItem item) {
        return new OrderItemDTO(
                item.getOrder().getId(),
                item.getProduct().getId(),
                item.getQuantity(),
                item.getTotalAmount()
        );
    }
}
