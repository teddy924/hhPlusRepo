package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.OrderCancelCommand;

@Schema(description = "주문취소 Request DTO")
public record OrderCancelRequestDTO (
        @Schema(description = "주문 ID") Long orderId
) {

    public OrderCancelCommand toCommand () {
        return new OrderCancelCommand(orderId);
    }

}
