package kr.hhplus.be.server.domain.order;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderAddressInfo (
        Long orderId,
        String receiverName,
        String phone,
        String address1,
        String address2,
        String zipcode,
        String memo,
        LocalDateTime sysCretDt,
        LocalDateTime sysChgDt
) {
}
