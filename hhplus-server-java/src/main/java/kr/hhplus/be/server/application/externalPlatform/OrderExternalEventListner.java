package kr.hhplus.be.server.application.externalPlatform;

import kr.hhplus.be.server.application.order.OrderExternalEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderExternalEventListner {

    private final OrderExternalEventPublisher orderExternalEventPublisher;

    @EventListener
    public void handle(OrderExternalCommand command) {
        orderExternalEventPublisher.publish(command);
    }
}
