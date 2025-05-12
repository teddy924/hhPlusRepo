package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderAddressInfo;
import lombok.Builder;

import java.util.Map;

@Builder
public record OrderCommand (
        Long userId,
        Map<Long, Integer> productGrp,
        Long couponId,
        OrderAddressInfo orderAddressInfo
) {

}
