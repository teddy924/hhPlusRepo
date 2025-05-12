package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.OrderAddress;
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
    public static OrderAddressInfo from(OrderAddress address) {
        return new OrderAddressInfo(
                address.getOrder().getId(),
                address.getReceiverName(),
                address.getPhone(),
                address.getAddress1(),
                address.getAddress2(),
                address.getZipcode(),
                address.getMemo(),
                address.getSysCretDt(),
                address.getSysChgDt()
        );
    }

}
