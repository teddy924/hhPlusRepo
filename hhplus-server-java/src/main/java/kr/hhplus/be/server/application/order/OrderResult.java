package kr.hhplus.be.server.application.order;

import lombok.Builder;

@Builder
public record OrderResult(
        Long orderId
) {
}
