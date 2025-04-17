package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import lombok.Builder;

@Builder
@Schema(description = "주문 상품 정보 DTO")
public record OrderItemDTO(
        Long productId,
        Integer quantity,
        Long totalAmount
) {
    public static OrderItemDTO from(OrderItem item) {
        return new OrderItemDTO(
                item.getProduct().getId(),
                item.getQuantity(),
                item.getTotalAmount()
        );
    }
}
