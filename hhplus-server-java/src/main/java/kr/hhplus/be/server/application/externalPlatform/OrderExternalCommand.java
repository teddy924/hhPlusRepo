package kr.hhplus.be.server.application.externalPlatform;

import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.Builder;

@Builder
public record OrderExternalCommand(
        Long orderId,
        OrderStatus status
) {
}
