package kr.hhplus.be.server.application.order.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.kafka.OrderKafkaPublisher;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.config.swagger.ErrorCode;
import kr.hhplus.be.server.infra.outbox.OutboxEvent;
import kr.hhplus.be.server.infra.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExternalEventPublisher {

    private final OrderKafkaPublisher orderKafkaPublisher;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public void publish(OrderExternalCommand command) {
//        ExternalRequest req = ExternalRequest.builder()
//                        .orderId(command.orderId())
//                        .status(command.status())
//                        .build();

//        log.info("external isSuccess : {}", externalClient.sendOrder(req).isSuccess());

//        log.info("orderEventPublish");
//        orderKafkaPublisher.sendOrderExternalCommand(command);

        try {
            // 1. ID 확보용 임시 저장
            OutboxEvent tmp = outboxService.saveOutboxEvent(
                    "ORDER",
                    String.valueOf(command.orderId()),
                    "ORDER_CREATED",
                    "TEMP"
            );

            // 2. ID 포함된 실제 command 생성
            OrderExternalCommand enriched = new OrderExternalCommand(
                    command.orderId(),
                    command.status(),
                    tmp.getId() // ID 주입
            );
            String finalPayload = objectMapper.writeValueAsString(enriched);

            // 3. insert-only 전략: 새로운 이벤트로 저장 (업데이트 없이)
            outboxService.saveOutboxEvent(
                    "ORDER",
                    String.valueOf(command.orderId()),
                    "ORDER_CREATED",
                    finalPayload
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.FAIL_SERIALIZATION);
        }
    }
}
