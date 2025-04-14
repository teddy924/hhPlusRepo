package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.entity.*;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderSaveInfo(
        Order order,
        OrderAddress orderAddress,
        List<OrderItem> orderItems,
        OrderCoupon orderCoupon,
        OrderHistory orderHistory
) {
}
