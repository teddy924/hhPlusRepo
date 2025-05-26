package kr.hhplus.be.server.infra.external;

import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.Builder;

@Builder
public class ExternalRequest {
    Long orderId;
    OrderStatus status;
}
