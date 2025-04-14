package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;

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
