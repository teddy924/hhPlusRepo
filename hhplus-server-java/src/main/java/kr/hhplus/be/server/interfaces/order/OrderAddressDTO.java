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

    public static OrderAddressDTO from(OrderAddressInfo info) {
        return new OrderAddressDTO(
                info.orderId(),
                info.receiverName(),
                info.phone(),
                info.address1(),
                info.address2(),
                info.zipcode(),
                info.memo(),
                info.sysCretDt(),
                info.sysChgDt());
    }
}
