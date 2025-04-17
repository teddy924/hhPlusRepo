package kr.hhplus.be.server.domain.order;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderInfo (
        Long userId,
        Map<Long, Long> productGrp,
        Long couponId,
        Long totPrice
) {
}
