package kr.hhplus.be.server.application.order;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderCommand (
        Long userId,
        Map<Long, Long> productGrp,
        Long couponId,
        OrderAddressInfo orderAddressInfo
) {

}
