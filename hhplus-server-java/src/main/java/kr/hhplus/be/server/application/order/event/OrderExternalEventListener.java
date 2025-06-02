package kr.hhplus.be.server.application.order.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderExternalEventListener {

    private final OrderExternalEventPublisher orderExternalEventPublisher;

    @TransactionalEventListener
    public void handle(OrderExternalCommand command) {
        orderExternalEventPublisher.publish(command);
    }
}
