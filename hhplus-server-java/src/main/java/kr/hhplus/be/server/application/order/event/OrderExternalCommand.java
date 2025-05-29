package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.Builder;

@Builder
public record OrderExternalCommand(
        Long orderId,
        OrderStatus status,
        Long outboxEventId
) {
}
