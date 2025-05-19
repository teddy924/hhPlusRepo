package kr.hhplus.be.server.application.externalPlatform;

import kr.hhplus.be.server.domain.order.OrderStatus;

public record OrderExternalCommand(
        Long orderId,
        OrderStatus status
) {
}
