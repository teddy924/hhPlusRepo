package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.application.order.kafka.OrderKafkaPublisher;
import kr.hhplus.be.server.infra.external.dataPlatform.ExternalClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExternalEventPublisher {

    private final ExternalClient externalClient;
    private final OrderKafkaPublisher orderKafkaPublisher;

    public void publish(OrderExternalCommand command) {
//        ExternalRequest req = ExternalRequest.builder()
//                        .orderId(command.orderId())
//                        .status(command.status())
//                        .build();

//        log.info("external isSuccess : {}", externalClient.sendOrder(req).isSuccess());

        log.info("orderEventPublish");
        orderKafkaPublisher.sendOrderExternalCommand(command);
    }
}
