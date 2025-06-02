package kr.hhplus.be.server.application.order.kafka;

import kr.hhplus.be.server.application.order.event.OrderExternalCommand;

public interface OrderKafkaPublisher {

    void sendOrderExternalCommand(OrderExternalCommand orderExternalCommand);
}
