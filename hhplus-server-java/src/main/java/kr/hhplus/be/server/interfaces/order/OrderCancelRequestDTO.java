package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderCancelCommand;

public record OrderCancelRequestDTO (
        Long orderId
) {

    public OrderCancelCommand toCommand () {
        return new OrderCancelCommand(orderId);
    }

}
