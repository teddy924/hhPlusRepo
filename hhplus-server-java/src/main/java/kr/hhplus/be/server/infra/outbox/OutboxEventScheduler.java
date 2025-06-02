package kr.hhplus.be.server.infra.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventScheduler {

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String ORDER_CREATED_TOPIC = "order.created.v1";
    private static final String ORDER_DLQ_TOPIC = ORDER_CREATED_TOPIC + ".dlq";
    private static final String COUPON_ISSUED_TOPIC = "coupon.FCIssued.v1";
    private static final String COUPON_ISSUED_DLQ_TOPIC = COUPON_ISSUED_TOPIC + ".dlq";

    @Scheduled(fixedDelay = 1000)
    public void publishOrderPendingEvents() {
        // 최근 5분 내 데이터만 전송 시도 (Optional)
        LocalDateTime timeCursor = LocalDateTime.now().minusMinutes(10);
        List<OutboxEvent> events = repository.findLatestDistinctEvents(
                List.of(OutboxStatus.PENDING.toString()),
                timeCursor
        );

        for (OutboxEvent event : events) {

            OutboxEvent.OutboxEventBuilder eventBuilder = OutboxEvent.builder()
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getEventType())
                    .payload(event.getPayload());

            try {
                kafkaTemplate.send(ORDER_CREATED_TOPIC, event.getAggregateId(), event.getPayload());
                repository.save(eventBuilder.status(OutboxStatus.SENT).build());
            } catch (Exception e) {
                log.error("Failed to send event: {}", event.getId(), e);
                repository.save(eventBuilder.status(OutboxStatus.FAILED).build());
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void publishOrderFailedToDLQ() {
        // 최근 5분 내 데이터만 전송 시도 (Optional)
        LocalDateTime timeCursor = LocalDateTime.now().minusMinutes(10);
        List<OutboxEvent> events = repository.findLatestDistinctEvents(
                List.of(OutboxStatus.FAILED_CONSUMER.toString()),
                timeCursor
        );

        for (OutboxEvent event : events) {

            OutboxEvent.OutboxEventBuilder eventBuilder = OutboxEvent.builder()
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getEventType())
                    .payload(event.getPayload());

            try {
                kafkaTemplate.send(ORDER_DLQ_TOPIC, event.getAggregateId(), event.getPayload());
                repository.save(eventBuilder.status(OutboxStatus.SENT).build());
            } catch (Exception e) {
                log.error("Failed to send event: {}", event.getId(), e);
                repository.save(eventBuilder.status(OutboxStatus.FAILED).build());
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void publishCouponIssuedEvents() {
        // 최근 5분 내 데이터만 전송 시도 (Optional)
        LocalDateTime timeCursor = LocalDateTime.now().minusMinutes(5);
        List<OutboxEvent> events = repository.findLatestDistinctEvents(
                List.of(OutboxStatus.PENDING.toString()),
                timeCursor
        );

        for (OutboxEvent event : events) {

            OutboxEvent.OutboxEventBuilder eventBuilder = OutboxEvent.builder()
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getEventType())
                    .payload(event.getPayload());

            try {
                kafkaTemplate.send(COUPON_ISSUED_TOPIC, event.getAggregateId(), event.getPayload());
                repository.save(eventBuilder.status(OutboxStatus.SENT).build());
            } catch (Exception e) {
                log.error("Failed to send event: {}", event.getId(), e);
                repository.save(eventBuilder.status(OutboxStatus.FAILED).build());
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void publishCouponIssueFailedToDLQ() {
        // 최근 5분 내 데이터만 전송 시도 (Optional)
        LocalDateTime timeCursor = LocalDateTime.now().minusMinutes(5);
        List<OutboxEvent> events = repository.findLatestDistinctEvents(
                List.of(OutboxStatus.PENDING.toString()),
                timeCursor
        );

        for (OutboxEvent event : events) {

            OutboxEvent.OutboxEventBuilder eventBuilder = OutboxEvent.builder()
                    .aggregateType(event.getAggregateType())
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getEventType())
                    .payload(event.getPayload());

            try {
                kafkaTemplate.send(COUPON_ISSUED_DLQ_TOPIC, event.getAggregateId(), event.getPayload());
                repository.save(eventBuilder.status(OutboxStatus.SENT).build());
            } catch (Exception e) {
                log.error("Failed to send event: {}", event.getId(), e);
                repository.save(eventBuilder.status(OutboxStatus.FAILED).build());
            }
        }
    }
}
