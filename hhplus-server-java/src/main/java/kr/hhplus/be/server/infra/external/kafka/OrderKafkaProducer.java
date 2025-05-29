package kr.hhplus.be.server.infra.external.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.event.OrderExternalCommand;
import kr.hhplus.be.server.application.order.kafka.OrderKafkaPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer implements OrderKafkaPublisher {

    private final KafkaTemplate<String, OrderExternalCommand> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String topic = "order.created.v1";

    @Override
    public void sendOrderExternalCommand(OrderExternalCommand command) {
        try {

            String json = objectMapper.writeValueAsString(command);

            log.info("order Kafka Producer sendOrderExternalCommand : {}", json);
            kafkaTemplate.send(topic, command);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize sendOrderExternalCommand : {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
