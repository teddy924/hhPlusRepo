package kr.hhplus.be.server.infra.external.kafka;

import kr.hhplus.be.server.application.order.kafka.OrderKafkaEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderKafkaEventHandler eventHandler;

    @KafkaListener(
            topics = "order.created.v1",
            groupId = "order.group",
            containerFactory = "orderCommandKafkaListenerContainerFactory"
    )
    public void consume(String message) {
        log.info("order Kafka Consumer consume");
        eventHandler.handleOrderExternalCommand(message);  // 책임 위임
    }
}
