package kr.hhplus.be.server.application.order.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.event.OrderExternalCommand;
import kr.hhplus.be.server.infra.external.dataPlatform.ExternalClient;
import kr.hhplus.be.server.infra.external.dataPlatform.ExternalRequest;
import kr.hhplus.be.server.infra.external.dataPlatform.ExternalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventHandler {

    private final ExternalClient externalClient;
    private final ObjectMapper objectMapper;

    public void handleOrderExternalCommand(String message) {
        try {
            OrderExternalCommand command = objectMapper.readValue(message, OrderExternalCommand.class);
            ExternalRequest request = mapToRequest(command);
            ExternalResponse response = externalClient.sendOrder(request);

            if (!response.isSuccess()) {
                throw new RuntimeException("외부 전송 실패");
            } else {
                log.info("external isSuccess: {}", response);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 역직렬화 실패", e);
        }
    }

    private ExternalRequest mapToRequest(OrderExternalCommand command) {
        return ExternalRequest.builder()
                .orderId(command.orderId())
                .status(command.status())
                .build();
    }

}
