package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderAddressInfo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderAddressDTO(
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
    public OrderAddressInfo toInfo() {
        return new OrderAddressInfo(orderId, receiverName, phone, address1, address2, zipcode, memo, sysCretDt, sysChgDt);
    }
}
