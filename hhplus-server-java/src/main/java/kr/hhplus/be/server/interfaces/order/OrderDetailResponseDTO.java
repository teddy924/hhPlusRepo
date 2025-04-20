package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.interfaces.payment.PaymentDTO;

import java.util.List;

@Schema(description = "주문 상세 Response DTO")
public record OrderDetailResponseDTO(
        @Schema(description = "주문 ID") Long orderId,
        @Schema(description = "주문 상품") List<OrderItemDTO> items,
        @Schema(description = "배송 정보") OrderAddressDTO address,
        @Schema(description = "결제 정보") PaymentDTO payment,
        @Schema(description = "주문 상태") OrderStatus status
) {
    public static OrderDetailResponseDTO from(Order order, List<OrderItemDTO> items, OrderAddressDTO address, PaymentDTO payment, OrderStatus status) {
        return new OrderDetailResponseDTO(
                order.getId(), items, address, payment, status
        );
    }
}
